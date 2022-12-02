package org.openmrs.module.authenticationui.page.controller.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.web.TotpAuthenticationScheme;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authenticationui.AuthenticationUiConfig;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.Security;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public class ConfigureTotpPageController extends AbstractAccountPageController {

    public String get(PageModel model,
                      @RequestParam(value = "userId", required = false) Integer userId,
                      @RequestParam(value = "schemeId", required = false) String schemeId,
                      @SpringBean("userService") UserService userService,
                      @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig) {

        userId = (userId == null ? Context.getAuthenticatedUser().getUserId() : userId);
        User user = userService.getUser(userId);
        try {
            checkPermissionAndAddToModel(authenticationUiConfig, user, model);
        }
        catch (Exception e) {
            return "redirect:/index.htm";
        }

        TotpAuthenticationScheme scheme = (TotpAuthenticationScheme) AuthenticationConfig.getAuthenticationScheme(schemeId);
        String secret = scheme.generateSecret();
        String qrCodeUri = scheme.generateQrCodeUriForSecret(secret, user.getUsername());

        model.addAttribute("schemeId", schemeId);
        model.addAttribute("secret", secret);
        model.addAttribute("qrCodeUri", qrCodeUri);

        return "account/configureTotp";
    }

    public String post(@RequestParam(value = "userId", required = false) Integer userId,
                       @RequestParam(value = "schemeId", required = false) String schemeId,
                       @RequestParam(value = "secret") String secret,
                       @RequestParam(value = "code", required = false) String code,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig,
                       HttpServletRequest request,
                       PageModel model) {

        userId = (userId == null ? Context.getAuthenticatedUser().getUserId() : userId);
        User user = userService.getUser(userId);

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
            setSuccessMessage(request, "authenticationui.configureTotp.success");
            return "redirect:authenticationui/account/userAccount.page?userId=" + user.getId();
        }
        catch (Exception e) {
            sendErrorMessage("authenticationui.configureTotp.fail", e, request);
        }

        return get(model, userId, schemeId, userService, authenticationUiConfig);
    }
}
