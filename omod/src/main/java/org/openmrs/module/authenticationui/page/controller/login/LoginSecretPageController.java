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
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.UserLogin;
import org.openmrs.module.authentication.web.AuthenticationSession;
import org.openmrs.module.authenticationui.AuthenticationUiModuleConfig;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;

public class LoginSecretPageController {

	public String get(
			PageModel pageModel,
			@SpringBean("userService") UserService userService,
			UiUtils ui,
			PageRequest request) {

		AuthenticationUiModuleConfig authenticationUiConfig = AuthenticationUiModuleConfig.getInstance();

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

		String question = userService.getSecretQuestion(candidateUser);

		if (StringUtils.isBlank(question)) {
			if (session.getErrorMessage() == null) {
				session.setErrorMessage("authentication.error.noSecretQuestionConfigured");
			}
			return "redirect:" + ui.pageLink("authenticationui", "login/login");
		}

		pageModel.put("authenticationUiConfig", authenticationUiConfig);
		pageModel.put("authenticationSession", session);
		pageModel.put("question", question);
		return null;
	}

	public String post(UiUtils ui) {
		return "redirect:" + AuthenticationUiModuleConfig.getInstance().getHomePageUrl(ui);
	}
}
