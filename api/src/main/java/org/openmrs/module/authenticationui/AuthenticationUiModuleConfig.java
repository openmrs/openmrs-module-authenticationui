/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.authenticationui;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ui.framework.UiUtils;

/**
 * This class provides a means to configure the behavior and overwrite defaults in this module with custom values
 */
public class AuthenticationUiModuleConfig {
	
	private static AuthenticationUiModuleConfig instance = new AuthenticationUiModuleConfig();

	private String headerLogoUrlProvider = "uicommons";
	private String headerLogoUrlResource = "images/logo/openmrs-with-title-small.png";
	private String homePageProvider = "";
	private String homePageResource = "";
	private boolean showLoginLocations = true;
	private String loginLocationTagName = "Login Location";
	private String lastLocationCookieName = "emr.lastSessionLocation";
	private String loginWelcomeMessage = "authenticationui.login.welcomeMessage";
	private String loginWarningIfNotChrome = "";
	private boolean allowPasswordReset = false;

	private AuthenticationUiModuleConfig() {
	}

	public static AuthenticationUiModuleConfig getInstance() {
		return instance;
	}

	// ***** static setters

	public static void setHeaderLogoUrlProvider(String headerLogoUrlProvider) {
		instance.headerLogoUrlProvider = headerLogoUrlProvider;
	}

	public static void setHeaderLogoUrlResource(String headerLogoUrlResource) {
		instance.headerLogoUrlResource = headerLogoUrlResource;
	}

	public static void setHomePageProvider(String homePageProvider) {
		instance.homePageProvider = homePageProvider;
	}

	public static void setHomePageResource(String homePageResource) {
		instance.homePageResource = homePageResource;
	}

	public static void setShowLoginLocations(boolean showLoginLocations) {
		instance.showLoginLocations = showLoginLocations;
	}

	public static void setLoginLocationTagName(String loginLocationTagName) {
		instance.loginLocationTagName = loginLocationTagName;
	}

	public static void setLastLocationCookieName(String lastLocationCookieName) {
		instance.lastLocationCookieName = lastLocationCookieName;
	}

	public static void setLoginWelcomeMessage(String loginWelcomeMessage) {
		instance.loginWelcomeMessage = loginWelcomeMessage;
	}

	public static void setLoginWarningIfNotChrome(String loginWarningIfNotChrome) {
		instance.loginWarningIfNotChrome = loginWarningIfNotChrome;
	}

	public static void setAllowPasswordReset(boolean allowPasswordReset) {
		instance.allowPasswordReset = allowPasswordReset;
	}

	// ***** instance getters

	public String getHeaderLogoUrl(UiUtils ui) {
		return getResourceUrl(ui, headerLogoUrlProvider, headerLogoUrlResource);
	}

	public String getHomePageUrl(UiUtils ui) {
		return getPageUrl(ui, homePageProvider, homePageResource);
	}

	public boolean isShowLoginLocations() {
		return showLoginLocations;
	}

	public String getLoginLocationTagName() {
		return loginLocationTagName;
	}

	public String getLastLocationCookieName() {
		return lastLocationCookieName;
	}

	public String getLoginWelcomeMessage() {
		return loginWelcomeMessage;
	}

	public String getLoginWarningIfNotChrome() {
		return loginWarningIfNotChrome;
	}

	public boolean isAllowPasswordReset() {
		return allowPasswordReset;
	}

	public String getPageUrl(UiUtils ui, String provider, String resource) {
		if (StringUtils.isBlank(provider)) {
			return "/" + ui.contextPath() + "/" + resource;
		}
		return ui.pageLink(provider, resource);
	}

	public String getResourceUrl(UiUtils ui, String provider, String resource) {
		if (StringUtils.isBlank(provider)) {
			return ui.resourceLink(resource);
		}
		return ui.resourceLink(provider, resource);
	}
}
