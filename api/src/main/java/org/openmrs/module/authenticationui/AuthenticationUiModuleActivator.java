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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;

import java.util.Properties;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class AuthenticationUiModuleActivator extends BaseModuleActivator {

	private static final Logger log = LogManager.getLogger(AuthenticationUiModuleActivator.class);

	public static final String MODULE_PREFIX = "authenticationui.";
	
	@Override
	public void started() {
		log.info("Authentication UI Module Started");
		Properties p = Context.getRuntimeProperties();
		for (String key : p.stringPropertyNames()) {
			if (key.equals(MODULE_PREFIX + "headerLogoUrl")) {
				AuthenticationUiModuleConfig.setHeaderLogoUrl(p.getProperty(key));
			}
			else if (key.equals(MODULE_PREFIX + "homePageUrl")) {
				AuthenticationUiModuleConfig.setHomePageUrl(p.getProperty(key));
			}
			else if (key.equals(MODULE_PREFIX + "showLoginLocations")) {
				AuthenticationUiModuleConfig.setShowLoginLocations(Boolean.parseBoolean(p.getProperty(key)));
			}
			else if (key.equals(MODULE_PREFIX + "requireLoginLocation")) {
				AuthenticationUiModuleConfig.setRequireLoginLocation(Boolean.parseBoolean(p.getProperty(key)));
			}
			else if (key.equals(MODULE_PREFIX + "loginLocationTagName")) {
				AuthenticationUiModuleConfig.setLoginLocationTagName(p.getProperty(key));
			}
			else if (key.equals(MODULE_PREFIX + "lastLocationCookieName")) {
				AuthenticationUiModuleConfig.setLastLocationCookieName(p.getProperty(key));
			}
			else if (key.equals(MODULE_PREFIX + "loginWelcomeMessage")) {
				AuthenticationUiModuleConfig.setLoginWelcomeMessage(p.getProperty(key));
			}
			else if (key.equals(MODULE_PREFIX + "loginWarningIfNotChrome")) {
				AuthenticationUiModuleConfig.setLoginWarningIfNotChrome(p.getProperty(key));
			}
			else if (key.equals(MODULE_PREFIX + "allowPasswordReset")) {
				AuthenticationUiModuleConfig.setAllowPasswordReset(Boolean.parseBoolean(p.getProperty(key)));
			}
			else if (key.equals(MODULE_PREFIX + "accountAdminPrivilege")) {
				AuthenticationUiModuleConfig.setAccountAdminPrivilege(p.getProperty(key));
			}
			else if (key.equals(MODULE_PREFIX + "phoneNumberPersonAttributeType")) {
				AuthenticationUiModuleConfig.setPhoneNumberPersonAttributeType(p.getProperty(key));
			}
			else if (key.equals(MODULE_PREFIX + "defaultLocationUserProperty")) {
				AuthenticationUiModuleConfig.setDefaultLocationUserProperty(p.getProperty(key));
			}
		}
	}
	
	@Override
	public void stopped() {
		log.info("Authentication UI Module Stopped");
	}
}
