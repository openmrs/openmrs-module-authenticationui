package org.openmrs.module.authenticationui.page.controller.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.AuthenticationScheme;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.web.AuthenticationSession;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authentication.web.WebAuthenticationScheme;
import org.openmrs.module.authenticationui.AuthenticationUiConfig;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                      @RequestParam(value = "userId", required = false) String userId,
                      @SpringBean("userService") UserService userService,
                      @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig) {

        User user = getUserOrAuthenticatedUser(userService, userId);
        try {
            checkPermissionAndAddToModel(authenticationUiConfig, user, model);
        }
        catch (Exception e) {
            return "redirect:/index.htm";
        }

        List<String> configuredSchemeIds = new ArrayList<>();
        model.addAttribute("configuredSchemeIds", configuredSchemeIds);

        Map<String, SecondaryOption> availableOptions = new LinkedHashMap<>();
        model.addAttribute("availableOptions", availableOptions);

        AuthenticationScheme authenticationScheme = AuthenticationConfig.getAuthenticationScheme();
        if (authenticationScheme instanceof TwoFactorAuthenticationScheme) {
            TwoFactorAuthenticationScheme scheme = (TwoFactorAuthenticationScheme) authenticationScheme;
            String existingOption = user.getUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE);
            if (StringUtils.isNotBlank(existingOption)) {
                configuredSchemeIds.addAll(Arrays.asList(existingOption.split(",")));
            }
            for (String schemeId : scheme.getSecondaryOptions()) {
                availableOptions.put(schemeId, new SecondaryOption(schemeId, user));
            }
        }

        return "account/twoFactorSetup";
    }

    public String post(@RequestParam(value = "userId", required = false) String userId,
                       @RequestParam(value = "schemeId", required = false) String schemeId,
                       @RequestParam(value = "remove", required = false) Boolean remove,
                       @RequestParam(value = "preferred", required = false) Boolean preferred,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig,
                       HttpServletRequest request,
                       HttpSession session,
                       PageModel model) {

        User user = getUserOrAuthenticatedUser(userService, userId);
        AuthenticationSession authenticationSession = new AuthenticationSession(session);

        try {
            checkPermissionAndAddToModel(authenticationUiConfig, user, model);
            boolean removeVal = remove == Boolean.TRUE;
            boolean preferredVal = preferred == Boolean.TRUE;

            AuthenticationScheme authenticationScheme = AuthenticationConfig.getAuthenticationScheme();
            TwoFactorAuthenticationScheme scheme = (TwoFactorAuthenticationScheme) authenticationScheme;
            List<String> schemeIds = scheme.getSecondaryAuthenticationSchemeIdsForUser(user);

            if (StringUtils.isNotBlank(schemeId)) {
                SecondaryOption option = new SecondaryOption(schemeId, user);

                boolean onlySettingPreferred = schemeIds.contains(schemeId) && preferredVal;

                // If this scheme requires configuration, redirect to the configuration page
                if (!onlySettingPreferred && !removeVal && StringUtils.isNotBlank(option.getConfigurationPage())) {
                    return "redirect:" + option.getConfigurationPage();
                }

                // Otherwise, add, remove and re-order as appropriate
                if (remove == Boolean.TRUE) {
                    schemeIds.remove(schemeId);
                }
                else if (!schemeIds.contains(schemeId)) {
                    schemeIds.add(schemeId);
                }

                if (preferredVal) {
                    schemeIds.remove(schemeId);
                    schemeIds.add(0, schemeId);
                }

                scheme.setSecondaryAuthenticationSchemeIdsForUser(user, schemeIds);

                userService.saveUser(user);
                authenticationSession.refreshAuthenticatedUser();
                setSuccessMessage(request, "authenticationui.configure2fa.success");
                return "redirect:authenticationui/account/twoFactorSetup.page?userId=" + user.getId();
            }
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

        public SecondaryOption(String schemeId, User user) {
            this.schemeId = schemeId;
            this.scheme = AuthenticationConfig.getAuthenticationScheme(schemeId);
            if (scheme instanceof WebAuthenticationScheme) {
                WebAuthenticationScheme webAuthenticationScheme = (WebAuthenticationScheme) scheme;
                this.configurationRequired = webAuthenticationScheme.isUserConfigurationRequired(user);
                String url = webAuthenticationScheme.getUserConfigurationPage();
                if (StringUtils.isNotBlank(url)) {
                    url = url.replace("{schemeId}", schemeId);
                    // If a user is not editing their own account, and the configuration page does not support a configurable userId, fail
                    boolean ownAccount = (user.equals(Context.getAuthenticatedUser()));
                    if (!ownAccount) {
                        if (!url.contains("{userId}")) {
                            throw new APIException("authenticationui.unauthorizedPageError");
                        }
                        url = url.replace("{userId}", user.getUserId().toString());
                    }
                    else {
                        url = url.replace("{userId}", "");
                    }
                }
                this.configurationPage = url;
            }
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
