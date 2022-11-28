package org.openmrs.module.authenticationui.page.controller.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.web.TotpAuthenticationScheme;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authenticationui.AuthenticationUiContext;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.Security;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public class ConfigureTotpPageController {

    public String get(PageModel model,
                      @RequestParam(value = "userId", required = false) Integer userId,
                      @RequestParam(value = "schemeId", required = false) String schemeId,
                      @SpringBean("userService") UserService userService,
                      @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                      HttpServletRequest request) {

        AuthenticationUiContext authenticationUiContext = new AuthenticationUiContext();

        User currentUser = Context.getAuthenticatedUser();
        User userToSetup = currentUser;
        boolean isOwnAccount = true;
        if (userId != null) {
            if (currentUser.hasPrivilege(authenticationUiContext.getConfig().getAccountAdminPrivilege())) {
                userToSetup = userService.getUser(userId);
                isOwnAccount = false;
            }
            else {
                String msg = messageSourceService.getMessage("emr.user.unauthorizedPageError");
                request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, msg);
                return "redirect:index.htm";
            }
        }

        TotpAuthenticationScheme scheme = (TotpAuthenticationScheme) AuthenticationConfig.getAuthenticationScheme(schemeId);
        String secret = scheme.generateSecret();
        String qrCodeUri = scheme.generateQrCodeUriForSecret(secret, userToSetup.getUsername());

        model.addAttribute("isOwnAccount", isOwnAccount);
        model.addAttribute("userToSetup", userToSetup);
        model.addAttribute("schemeId", schemeId);
        model.addAttribute("secret", secret);
        model.addAttribute("qrCodeUri", qrCodeUri);

        return "account/configureTotp";
    }

    public String post(@RequestParam(value = "userId", required = false) Integer userId,
                       @RequestParam(value = "schemeId", required = false) String schemeId,
                       @RequestParam(value = "secret") String secret,
                       @RequestParam(value = "code") String code,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                       HttpServletRequest request,
                       UiUtils ui,
                       PageModel model) {

        AuthenticationUiContext authenticationUiContext = new AuthenticationUiContext();

        // First ensure that if someone is editing someone else's account, that they are authorized
        User currentUser = Context.getAuthenticatedUser();
        User userToSetup = currentUser;
        boolean isOwnAccount = true;
        if (userId != null) {
            if (currentUser.hasPrivilege(authenticationUiContext.getConfig().getAccountAdminPrivilege())) {
                userToSetup = userService.getUser(userId);
                isOwnAccount = false;
            }
            else {
                String msg = messageSourceService.getMessage("emr.user.unauthorizedPageError");
                request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, msg);
                return "redirect:index.htm";
            }
        }

        model.addAttribute("isOwnAccount", isOwnAccount);
        model.addAttribute("userToSetup", userToSetup);
        model.addAttribute("schemeId", schemeId);

        TotpAuthenticationScheme scheme = (TotpAuthenticationScheme) AuthenticationConfig.getAuthenticationScheme(schemeId);
        try {
            if (StringUtils.isBlank(code) || StringUtils.isBlank(secret)) {
                throw new RuntimeException("authentication.totp.code.required");
            }
            boolean isValidCode = scheme.verifyCode(secret, code);
            if (!isValidCode) {
                throw new RuntimeException("authentication.totp.code.invalid");
            }
            userService.setUserProperty(userToSetup, scheme.getSecretUserPropertyName(), Security.encrypt(secret));
            userToSetup.setUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE, schemeId);
            userService.saveUser(userToSetup);

            String msg = messageSourceService.getMessage("authentication.totp.setup.success", null, Context.getLocale());
            request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, msg);
            request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
        }
        catch (Exception e) {
            String msg = messageSourceService.getMessage("authentication.totp.setup.fail");
            String reason =messageSourceService.getMessage(e.getMessage());
            request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, msg + ": " + reason);

            secret = scheme.generateSecret();
            model.addAttribute("secret", secret);
            model.addAttribute("qrCodeUri", scheme.generateQrCodeUriForSecret(secret, userToSetup.getUsername()));
            return "account/configureTotp";
        }

        String returnUrl = (isOwnAccount ? "myAccount.page" : "account.page?personId=" + userToSetup.getPerson().getPersonId());
        return "redirect:" + ui.pageLink("authenticationui", "account/" + returnUrl);
    }
}