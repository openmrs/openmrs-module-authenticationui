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

package org.openmrs.module.authenticationui.page.controller.login;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.context.AuthenticationScheme;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.UserLogin;
import org.openmrs.module.authentication.web.AuthenticationSession;
import org.openmrs.module.authentication.web.EmailAuthenticationScheme;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authentication.web.WebAuthenticationScheme;
import org.openmrs.module.authenticationui.AuthenticationUiConfig;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;

public class LoginEmailPageController {

	public String get(
			PageModel pageModel,
			@SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig,
			UiUtils ui,
			PageRequest request) {

		if (Context.isAuthenticated()) {
			return "redirect:" + authenticationUiConfig.getHomePageUrl(ui);
		}

		AuthenticationSession session = new AuthenticationSession(request.getRequest(), request.getResponse());
		UserLogin authenticationContext = session.getUserLogin();
		User candidateUser = authenticationContext.getUser();

		if (candidateUser == null) {
			if (session.getErrorMessage() == null) {
				session.setErrorMessage("authentication.error.candidateUserRequired");
			}
			return "redirect:" + ui.pageLink("authenticationui", "login/login");
		}

		String email = null;
		AuthenticationScheme scheme = AuthenticationConfig.getAuthenticationScheme();
		if (scheme instanceof TwoFactorAuthenticationScheme) {
			TwoFactorAuthenticationScheme tfaScheme = (TwoFactorAuthenticationScheme) scheme;
			WebAuthenticationScheme secondaryScheme = tfaScheme.getSecondaryAuthenticationScheme(session, candidateUser);
			if (secondaryScheme instanceof EmailAuthenticationScheme) {
				EmailAuthenticationScheme emailScheme = (EmailAuthenticationScheme) secondaryScheme;
				email = emailScheme.getVerifiedEmailForUser(candidateUser);
			}
		}

		if (StringUtils.isBlank(email)) {
			if (session.getErrorMessage() == null) {
				session.setErrorMessage("authentication.error.noEmailConfiguredForUser");
			}
			return "redirect:" + ui.pageLink("authenticationui", "login/login");
		}

		pageModel.put("authenticationUiConfig", authenticationUiConfig);
		pageModel.put("authenticationSession", session);
		pageModel.put("email", email);
		return null;
	}

	public String post(UiUtils ui, @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig) {
		return "redirect:" + authenticationUiConfig.getHomePageUrl(ui);
	}
}
