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
import org.openmrs.api.context.AuthenticationScheme;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.UserLogin;
import org.openmrs.module.authentication.web.AuthenticationSession;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authentication.web.WebAuthenticationScheme;
import org.openmrs.module.authenticationui.AuthenticationUiConfig;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class TwoFactorAlternativesFragmentController {

    protected final Log log = LogFactory.getLog(getClass());

    public void controller(@SpringBean("userService") UserService userService,
                           @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig,
                           HttpServletRequest request, HttpServletResponse response,
                           FragmentConfiguration config, FragmentModel model, UiUtils ui) {

        List<String> schemeIds = new ArrayList<>();
        AuthenticationScheme authenticationScheme = AuthenticationConfig.getAuthenticationScheme();
        if (authenticationScheme instanceof TwoFactorAuthenticationScheme) {
            TwoFactorAuthenticationScheme tfaScheme = (TwoFactorAuthenticationScheme) authenticationScheme;
            AuthenticationSession session = new AuthenticationSession(request, response);
            UserLogin authenticationContext = session.getUserLogin();
            User candidateUser = authenticationContext.getUser();
            WebAuthenticationScheme current2faScheme = tfaScheme.getSecondaryAuthenticationScheme(session, candidateUser);
            List<String> tfaSchemes = tfaScheme.getSecondaryAuthenticationSchemeIdsForUser(candidateUser);
            schemeIds.addAll(tfaSchemes);
            schemeIds.remove(current2faScheme.getSchemeId());
        }
        model.addAttribute("homePageUrl", authenticationUiConfig.getHomePageUrl(ui));
        model.addAttribute("schemeIds", schemeIds);
    }

    public FragmentActionResult changeSecondaryAuthenticationSchemeForSession(@RequestParam("schemeId") String schemeId, UiUtils ui,
                                                                           HttpServletRequest request, HttpServletResponse response) {

        try {
            Context.addProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
            AuthenticationScheme authenticationScheme = AuthenticationConfig.getAuthenticationScheme();
            TwoFactorAuthenticationScheme tfaScheme = (TwoFactorAuthenticationScheme) authenticationScheme;
            AuthenticationSession session = new AuthenticationSession(request, response);
            tfaScheme.setSecondaryAuthenticationSchemeForSession(session, schemeId);
            return new SuccessResult(ui.message("authenticationui.configure2fa.success"));
        }
        catch (Exception e) {
            return new FailureResult(ui.message("authenticationui.configure2fa.fail", e.getMessage()));
        }
        finally {
            Context.removeProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
        }
    }
}
