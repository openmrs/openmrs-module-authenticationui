package org.openmrs.module.authenticationui.page.controller.admin;

import org.openmrs.module.authentication.UserLoginTracker;
import org.openmrs.module.authenticationui.AuthenticationUiContext;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;

import java.io.IOException;

/**
 * Administrative page to view currently logged-in users
 */
public class ActiveUsersPageController {

    public String get(PageModel model, UiUtils ui) throws IOException {
        AuthenticationUiContext authenticationUiContext = new AuthenticationUiContext();
        if (!authenticationUiContext.hasPrivilege(authenticationUiContext.getConfig().getAccountAdminPrivilege())) {
            return "redirect:" + authenticationUiContext.getConfig().getHomePageUrl(ui);
        }
        model.addAttribute("authenticationUiContext", authenticationUiContext);
        model.addAttribute("activeUsers", UserLoginTracker.getActiveLogins().values());
        return "admin/activeUsers";
    }
}
