/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.authenticationui.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.authenticationui.AuthenticationUiModuleConfig;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountBreadcrumbsFragmentController {

    protected final Log log = LogFactory.getLog(getClass());

    public void controller(@SpringBean("userService") UserService userService,
                           FragmentConfiguration config, FragmentModel model, UiUtils ui) {

        Integer userId = (Integer) config.getAttribute("userId");
        String label = (String) config.get("label");

        User authenticatedUser = Context.getAuthenticatedUser();
        boolean ownAccount = (userId == null || userId.equals(authenticatedUser.getUserId()));

        List<Map<String, String>> breadcrumbs = new ArrayList<>();

        AuthenticationUiModuleConfig authConfig = AuthenticationUiModuleConfig.getInstance();

        addBreadcrumb(breadcrumbs, "icon-home", "", authConfig.getHomePageUrl(ui));
        if (!ownAccount) {
            addBreadcrumb(breadcrumbs, "", ui.message("authenticationui.systemAdministration.title"), authConfig.getAdminPageUrl(ui));
            addBreadcrumb(breadcrumbs, "", ui.message("authenticationui.manageAccounts.title"), authConfig.getManageUsersUrl(ui));
        }

        if (userId != null) {
            User user = userService.getUser(userId);
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            String userLabel = ownAccount ? ui.message("authenticationui.myAccount.title") : ui.format(user.getPerson());
            String userLink = (label == null ? "" : ui.pageLink("authenticationui", "account/userAccount", params));
            addBreadcrumb(breadcrumbs, "", userLabel, userLink);
        }
        if (label != null) {
            addBreadcrumb(breadcrumbs, "", ui.message(label), "");
        }
        model.addAttribute("breadcrumbs", ui.toJson(breadcrumbs));
    }

    private void addBreadcrumb(List<Map<String, String>> breadcrumbs, String icon, String label, String link) {
        Map<String, String> breadcrumb = new HashMap<>();
        breadcrumb.put("icon", icon);
        breadcrumb.put("label", label);
        breadcrumb.put("link", link);
        breadcrumbs.add(breadcrumb);
    }
}
