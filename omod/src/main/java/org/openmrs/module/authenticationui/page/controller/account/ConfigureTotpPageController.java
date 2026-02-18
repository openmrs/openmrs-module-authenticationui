package org.openmrs.module.authenticationui.page.controller.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.web.AuthenticationSession;
import org.openmrs.module.authentication.web.TotpAuthenticationScheme;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authenticationui.AuthenticationUiConfig;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.util.Security;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ConfigureTotpPageController extends AbstractAccountPageController {

    @Override
    protected void checkPermissionAndAddToModel(AuthenticationUiConfig authenticationUiConfig, User user, PageModel model) {
        super.checkPermissionAndAddToModel(authenticationUiConfig, user, model);
        boolean ownAccount = (user.equals(Context.getAuthenticatedUser()));
        if (!ownAccount && !Context.hasPrivilege(PrivilegeConstants.EDIT_USER_PASSWORDS)) {
            throw new APIException("authenticationui.unauthorizedPageError");
        }
    }

    public String get(PageModel model,
                      @RequestParam(value = "userId", required = false) String userId,
                      @RequestParam(value = "schemeId", required = false) String schemeId,
                      @SpringBean("userService") UserService userService,
                      @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig) {

        User user = getUserOrAuthenticatedUser(userService, userId);
        try {
            checkPermissionAndAddToModel(authenticationUiConfig, user, model);
        }
        catch (Exception e) {
            return "redirect:/index.htm";
        }

        TotpAuthenticationScheme scheme = (TotpAuthenticationScheme) AuthenticationConfig.getAuthenticationScheme(schemeId);
        String secret = (String) model.getOrDefault("secret", scheme.generateSecret());
        String qrCodeUri = scheme.generateQrCodeUriForSecret(secret, user.getUsername());

        model.addAttribute("schemeId", schemeId);
        model.addAttribute("secret", secret);
        model.addAttribute("qrCodeUri", qrCodeUri);

        return "account/configureTotp";
    }

    public String post(@RequestParam(value = "userId", required = false) String userId,
                       @RequestParam(value = "schemeId", required = false) String schemeId,
                       @RequestParam(value = "secret") String secret,
                       @RequestParam(value = "code", required = false) String code,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig,
                       HttpServletRequest request,
                       HttpSession session,
                       PageModel model) {

        User user = getUserOrAuthenticatedUser(userService, userId);
        boolean ownAccount = (user.equals(Context.getAuthenticatedUser()));

        AuthenticationSession authenticationSession = new AuthenticationSession(session);

        TotpAuthenticationScheme scheme = (TotpAuthenticationScheme) AuthenticationConfig.getAuthenticationScheme(schemeId);
        try {
            checkPermissionAndAddToModel(authenticationUiConfig, user, model);
            if (StringUtils.isBlank(code) || StringUtils.isBlank(secret)) {
                throw new RuntimeException("authenticationui.configureTotp.code.required");
            }
            boolean isValidCode = scheme.verifyCode(secret, code);
            if (!isValidCode) {
                throw new RuntimeException("authenticationui.configureTotp.code.invalid");
            }
            userService.setUserProperty(user, scheme.getSecretUserPropertyName(), Security.encrypt(secret));
            user.setUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE, schemeId);
            userService.saveUser(user);
            if (ownAccount) {
                authenticationSession.refreshAuthenticatedUser();
            }
            setSuccessMessage(request, "authenticationui.configureTotp.success");
            return "redirect:authenticationui/account/userAccount.page?userId=" + user.getId();
        }
        catch (Exception e) {
            sendErrorMessage("authenticationui.configureTotp.fail", e, request);
        }

        model.addAttribute("secret", secret);
        return get(model, userId, schemeId, userService, authenticationUiConfig);
    }
}
