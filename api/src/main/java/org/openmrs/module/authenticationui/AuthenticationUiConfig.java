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

import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.AppUiExtensions;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class provides a means to configure the behavior and overwrite defaults in this module with custom values
 */
@Component
public class AuthenticationUiConfig {

	public static final String HEADER_EXTENSION = AppUiExtensions.HEADER_CONFIG_EXTENSION;
	public static final String HEADER_ICON_URL = "logo-icon-url";
	public static final String HEADER_LINK_URL = "logo-link-url";

	public static final String ADMIN_EXTENSION = "org.openmrs.module.authenticationui.admin.config";
	public static final String ADMIN_PAGE_URL = "admin-page-url";
	public static final String ADMIN_MANAGE_USERS_PAGE_URL = "manage-users-page-url";
	public static final String ADMIN_EDIT_USER_PAGE_URL = "admin-edit-user-page-url";
	public static final String ADMIN_REQUIRED_PRIVILEGE = "required-privilege";
	public static final String ADMIN_PHONE_ATTRIBUTE_TYPE = "phone-number-person-attribute-type";
	public static final String ADMIN_DEFAULT_LOCATION_USER_PROPERTY = "default-location-user-property";

	public static final String LOGIN_PAGE_EXTENSION = "org.openmrs.module.authenticationui.loginPage.config";
	public static final String LOGIN_SHOW_LOCATIONS = "show-locations";
	public static final String LOGIN_REQUIRE_LOCATION = "require-location";
	public static final String LOGIN_LOCATION_TAG_NAME = "location-tag-name";
	public static final String LOGIN_LAST_LOCATION_COOKIE_NAME = "last-location-cookie-name";
	public static final String LOGIN_WELCOME_MESSAGE = "welcome-message";
	public static final String LOGIN_WARNING_IF_NOT_CHROME = "warning-if-not-chrome";
	public static final String LOGIN_ALLOW_PASSWORD_RESET = "allow-password-reset";

	AppFrameworkService appFrameworkService;

	@Autowired
	public AuthenticationUiConfig(AppFrameworkService appFrameworkService) {
		this.appFrameworkService = appFrameworkService;
	}

	public String getHeaderLogoUrl(UiUtils ui) {
		return getResourceUrl(ui, getConfig(HEADER_EXTENSION, HEADER_ICON_URL, "uicommons:images/logo/openmrs-with-title-small.png"));
	}

	public String getHomePageUrl(UiUtils ui) {
		return getPageUrl(ui, getConfig(HEADER_EXTENSION, HEADER_LINK_URL, ""));
	}

	public boolean isShowLoginLocations() {
		return getConfig(LOGIN_PAGE_EXTENSION, LOGIN_SHOW_LOCATIONS, true);
	}

	public boolean isRequireLoginLocation() {
		return getConfig(LOGIN_PAGE_EXTENSION, LOGIN_REQUIRE_LOCATION, true);
	}

	public String getLoginLocationTagName() {
		return getConfig(LOGIN_PAGE_EXTENSION, LOGIN_LOCATION_TAG_NAME, "Login Location");
	}

	public String getLastLocationCookieName() {
		return getConfig(LOGIN_PAGE_EXTENSION, LOGIN_LAST_LOCATION_COOKIE_NAME, "emr.lastSessionLocation");
	}

	public String getLoginWelcomeMessage() {
		return getConfig(LOGIN_PAGE_EXTENSION, LOGIN_WELCOME_MESSAGE, "authenticationui.login.welcomeMessage");
	}

	public String getLoginWarningIfNotChrome() {
		return getConfig(LOGIN_PAGE_EXTENSION, LOGIN_WARNING_IF_NOT_CHROME, "");
	}

	public boolean isAllowPasswordReset() {
		return getConfig(LOGIN_PAGE_EXTENSION, LOGIN_ALLOW_PASSWORD_RESET, false);
	}

	public String getAdminPageUrl(UiUtils ui) {
		return getPageUrl(ui, getConfig(ADMIN_EXTENSION, ADMIN_PAGE_URL, "/admin/index.htm"));
	}

	public String getManageUsersUrl(UiUtils ui) {
		return getPageUrl(ui, getConfig(ADMIN_EXTENSION, ADMIN_MANAGE_USERS_PAGE_URL, "/admin/users/users.list"));
	}

	public String getAdminEditUserPageUrl(UiUtils ui, Integer userId) {
		return getPageUrl(ui, getConfig(ADMIN_EXTENSION, ADMIN_EDIT_USER_PAGE_URL, "authenticationui:account/userAccount"), "userId", userId);
	}

	public String getAccountAdminPrivilege() {
		return getConfig(ADMIN_EXTENSION, ADMIN_REQUIRED_PRIVILEGE, PrivilegeConstants.EDIT_USERS);
	}

	public String getPhoneNumberPersonAttributeType() {
		return getConfig(ADMIN_EXTENSION, ADMIN_PHONE_ATTRIBUTE_TYPE, null);
	}

	public String getDefaultLocationUserProperty() {
		return getConfig(ADMIN_EXTENSION, ADMIN_DEFAULT_LOCATION_USER_PROPERTY, OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION);
	}

	protected <T> T getConfig(String extensionPointId, String propertyName, T defaultValue) {
		Extension ext = getLowestOrderExtension(extensionPointId);
		T setting = (T) getConfigSetting(ext, propertyName);
		return setting == null ? defaultValue : setting;
	}

	protected Extension getLowestOrderExtension(String extensionPointId) {
		Extension lowestOrderExtension = null;
		for (Extension extension : appFrameworkService.getExtensionsForCurrentUser(extensionPointId)) {
			if (lowestOrderExtension == null || extension.getOrder() < lowestOrderExtension.getOrder()) {
				lowestOrderExtension = extension;
			}
		}
		return lowestOrderExtension;
	}

	protected Object getConfigSetting(Extension extension, String propertyName) {
		if (extension != null && extension.getExtensionParams() != null) {
			return extension.getExtensionParams().get(propertyName);
		}
		return null;
	}

	protected String getPageUrl(UiUtils ui, String url, Object... params) {
		String pageUrl = "";
		String[] providerAndResource = url.split(":");
		if (providerAndResource.length == 1) {
			pageUrl = "/" + ui.contextPath() + (url.startsWith("/") ? "" : "/") + url;
		}
		else {
			pageUrl = ui.pageLink(providerAndResource[0], providerAndResource[1]);
		}
		if (params.length > 0) {
			for (int i=0; i<params.length; i+=2) {
				pageUrl += (pageUrl.contains("?") ? "&" : "?") + params[i] + "=" + params[i+1];
			}
		}
		return pageUrl;
	}

	protected String getResourceUrl(UiUtils ui, String url) {
		String[] providerAndResource = url.split(":");
		if (providerAndResource.length == 1) {
			return "/" + ui.contextPath() + (url.startsWith("/") ? "" : "/") + url;
		}
		return ui.resourceLink(providerAndResource[0], providerAndResource[1]);
	}
}
