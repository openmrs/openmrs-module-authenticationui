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
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides a means to configure the behavior and overwrite defaults in this module with custom values
 */
public class AuthenticationUiModuleConfig {
	
	private static final AuthenticationUiModuleConfig instance = new AuthenticationUiModuleConfig();

	private String headerLogoUrl = "uicommons:images/logo/openmrs-with-title-small.png";
	private String homePageUrl = "";
	private boolean showLoginLocations = true;
	private String loginLocationTagName = "Login Location";
	private String lastLocationCookieName = "emr.lastSessionLocation";
	private String loginWelcomeMessage = "authenticationui.login.welcomeMessage";
	private String loginWarningIfNotChrome = "";
	private boolean allowPasswordReset = false;
	private String accountAdminPrivilege = PrivilegeConstants.EDIT_USERS;
	private String phoneNumberPersonAttributeType = null;
	private String defaultLocationUserProperty = OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION;
	private List<Map<String, String>> accountBreadcrumbs = new ArrayList<>();

	private AuthenticationUiModuleConfig() {
	}

	public static AuthenticationUiModuleConfig getInstance() {
		return instance;
	}

	// ***** static setters

	public static void setHeaderLogoUrl(String headerLogoUrl) {
		instance.headerLogoUrl = headerLogoUrl;
	}

	public static void setHomePageUrl(String homePageUrl) {
		instance.homePageUrl = homePageUrl;
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

	public static void setAccountAdminPrivilege(String accountAdminPrivilege) {
		instance.accountAdminPrivilege = accountAdminPrivilege;
	}

	public static void setPhoneNumberPersonAttributeType(String phoneNumberPersonAttributeType) {
		instance.phoneNumberPersonAttributeType = phoneNumberPersonAttributeType;
	}

	public static void setDefaultLocationUserProperty(String defaultLocationUserProperty) {
		instance.defaultLocationUserProperty = defaultLocationUserProperty;
	}

	public static void addAccountBreadcrumb(String labelCode, String link) {
		Map<String, String> breadcrumb = new HashMap<>();
		breadcrumb.put("label", labelCode);
		breadcrumb.put("link", link);
		instance.accountBreadcrumbs.add(breadcrumb);
	}

	// ***** instance getters

	public String getHeaderLogoUrl(UiUtils ui) {
		return getResourceUrl(ui, headerLogoUrl);
	}

	public String getHomePageUrl(UiUtils ui) {
		return getPageUrl(ui, homePageUrl);
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

	public String getAccountAdminPrivilege() {
		return accountAdminPrivilege;
	}

	public String getPhoneNumberPersonAttributeType() {
		return phoneNumberPersonAttributeType;
	}

	public String getDefaultLocationUserProperty() {
		return defaultLocationUserProperty;
	}

	public List<Map<String, String>> getAccountBreadcrumbs(UiUtils ui) {
		List<Map<String, String>> ret = new ArrayList<>();
		for (Map<String, String> m : accountBreadcrumbs) {
			Map<String, String> breadcrumb = new HashMap<>();
			String label = m.get("label");
			String icon = m.get("icon");
			String link = m.get("link");
			breadcrumb.put("label", StringUtils.isBlank(label) ? "" : ui.message(label));
			breadcrumb.put("icon", icon == null ? "" : icon);
			breadcrumb.put("link", StringUtils.isBlank(link) ? "" : getPageUrl(ui, link));
			ret.add(breadcrumb);
		}
		return ret;
	}

	public String getPageUrl(UiUtils ui, String url) {
		String[] providerAndResource = url.split(":");
		if (providerAndResource.length == 1) {
			return "/" + ui.contextPath() + "/" + url;
		}
		return ui.pageLink(providerAndResource[0], providerAndResource[1]);
	}

	public String getResourceUrl(UiUtils ui, String url) {
		String[] providerAndResource = url.split(":");
		if (providerAndResource.length == 1) {
			return ui.resourceLink(url);
		}
		return ui.resourceLink(providerAndResource[0], providerAndResource[1]);
	}
}
