package org.openmrs.module.authenticationui.page.controller.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.AuthenticationScheme;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authentication.web.WebAuthenticationScheme;
import org.openmrs.module.authenticationui.AuthenticationUiConfig;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class TwoFactorSetupPageController extends AbstractAccountPageController {

    @Override
    protected void checkPermissionAndAddToModel(AuthenticationUiConfig authenticationUiConfig, User user, PageModel model) {
        super.checkPermissionAndAddToModel(authenticationUiConfig, user, model);
        boolean ownAccount = (user.equals(Context.getAuthenticatedUser()));
        if (!ownAccount && !Context.hasPrivilege(PrivilegeConstants.EDIT_USER_PASSWORDS)) {
            throw new APIException("authenticationui.unauthorizedPageError");
        }
    }

    public String get(PageModel model,
                      @RequestParam(value = "userId", required = false) Integer userId,
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

        AuthenticationScheme authenticationScheme = AuthenticationConfig.getAuthenticationScheme();
        boolean twoFactorAvailable = (authenticationScheme instanceof TwoFactorAuthenticationScheme);
        model.addAttribute("twoFactorAvailable", twoFactorAvailable);

        String existingOption = user.getUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE);
        model.addAttribute("existingOption", existingOption);

        if (twoFactorAvailable) {
            TwoFactorAuthenticationScheme scheme = (TwoFactorAuthenticationScheme) authenticationScheme;
            List<SecondaryOption> secondaryOptions = new ArrayList<>();
            for (String schemeId : scheme.getSecondaryOptions()) {
                SecondaryOption option = new SecondaryOption(schemeId);
                if (option.getScheme() instanceof WebAuthenticationScheme) {
                    WebAuthenticationScheme webAuthenticationScheme = (WebAuthenticationScheme) option.getScheme();
                    option.setConfigurationRequired(webAuthenticationScheme.isUserConfigurationRequired(user));
                    option.setConfigurationPage(webAuthenticationScheme.getUserConfigurationPage());
                }
                option.setCurrentlySelected(schemeId.equalsIgnoreCase(existingOption));
                secondaryOptions.add(option);
            }
            model.addAttribute("secondaryOptions", secondaryOptions);
        }

        return "account/twoFactorSetup";
    }

    public String post(@RequestParam(value = "userId", required = false) Integer userId,
                       @RequestParam(value = "schemeId", required = false) String schemeId,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig,
                       HttpServletRequest request,
                       PageModel model) {

        userId = (userId == null ? Context.getAuthenticatedUser().getUserId() : userId);
        User user = userService.getUser(userId);
        boolean ownAccount = (user.equals(Context.getAuthenticatedUser()));

        try {
            checkPermissionAndAddToModel(authenticationUiConfig, user, model);

            // If this scheme requires configuration, redirect to the configuration page
            if (StringUtils.isNotBlank(schemeId)) {
                AuthenticationScheme scheme = AuthenticationConfig.getAuthenticationScheme(schemeId);
                if (scheme instanceof WebAuthenticationScheme) {
                    WebAuthenticationScheme webScheme = (WebAuthenticationScheme) scheme;
                    if (StringUtils.isNotBlank(webScheme.getUserConfigurationPage())) {
                        String url = webScheme.getUserConfigurationPage().replace("{schemeId}", schemeId);
                        // If a user is not editing their own account, and the configuration page does not support a configurable userId, fail
                        if (!ownAccount) {
                            if (!url.contains("{userId}")) {
                                throw new APIException("authenticationui.unauthorizedPageError");
                            }
                            url = url.replace("{userId}", user.getUserId().toString());
                        }
                        else {
                            url = url.replace("{userId}", "");
                        }
                        return "redirect:" + url;
                    }
                }
            }
            // Otherwise, assume no configuration is needed and set this as the two factor method
            if (StringUtils.isBlank(schemeId)) {
                user.removeUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE);
            }
            else {
                user.setUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE, schemeId);
            }
            userService.saveUser(user);
            setSuccessMessage(request, "authenticationui.configure2fa.success");
            return "redirect:authenticationui/account/userAccount.page?userId=" + user.getId();
        }
        catch (Exception e) {
            sendErrorMessage("authenticationui.configure2fa.fail", e, request);
        }

        return get(model, userId, userService, authenticationUiConfig);
    }

    public static class SecondaryOption {

        private final String schemeId;
        private final AuthenticationScheme scheme;
        private boolean configurationRequired = false;
        private String configurationPage;
        private boolean currentlySelected = false;

        public SecondaryOption(String schemeId) {
            this.schemeId = schemeId;
            this.scheme = AuthenticationConfig.getAuthenticationScheme(schemeId);
        }

        public String getSchemeId() {
            return schemeId;
        }

        public AuthenticationScheme getScheme() {
            return scheme;
        }

        public boolean isConfigurationRequired() {
            return configurationRequired;
        }

        public void setConfigurationRequired(boolean configurationRequired) {
            this.configurationRequired = configurationRequired;
        }

        public String getConfigurationPage() {
            return configurationPage;
        }

        public void setConfigurationPage(String configurationPage) {
            this.configurationPage = configurationPage;
        }

        public boolean isCurrentlySelected() {
            return currentlySelected;
        }

        public void setCurrentlySelected(boolean currentlySelected) {
            this.currentlySelected = currentlySelected;
        }
    }
}
