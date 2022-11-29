package org.openmrs.module.authenticationui.page.controller.admin;

import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.UserLoginTracker;
import org.openmrs.module.authenticationui.AuthenticationUiModuleConfig;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;

import java.io.IOException;

/**
 * Administrative page to view currently logged-in users
 */
public class ActiveUsersPageController {

    public String get(PageModel model, UiUtils ui) throws IOException {
        AuthenticationUiModuleConfig authenticationUiConfig = AuthenticationUiModuleConfig.getInstance();
        if (!Context.hasPrivilege(authenticationUiConfig.getAccountAdminPrivilege())) {
            return "redirect:" + authenticationUiConfig.getHomePageUrl(ui);
        }
        model.addAttribute("authenticationUiConfig", authenticationUiConfig);
        model.addAttribute("activeUsers", UserLoginTracker.getActiveLogins().values());
        return "admin/activeUsers";
    }
}
