package org.openmrs.module.authenticationui.page.controller.admin;

import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.UserLoginTracker;
import org.openmrs.module.authenticationui.AuthenticationUiConfig;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

/**
 * Administrative page to view currently logged-in users
 */
public class ActiveUsersPageController {

    public String get(PageModel model, UiUtils ui,
                      @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig) {
        if (!Context.hasPrivilege(authenticationUiConfig.getAccountAdminPrivilege())) {
            return "redirect:" + authenticationUiConfig.getHomePageUrl(ui);
        }
        model.addAttribute("authenticationUiConfig", authenticationUiConfig);
        model.addAttribute("activeUsers", UserLoginTracker.getActiveLogins().values());
        return "admin/activeUsers";
    }
}
