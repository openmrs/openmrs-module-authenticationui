/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.authenticationui.page.controller.account;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authenticationui.AuthenticationUiModuleConfig;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.LocaleUtility;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

public class MyAccountPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public void get(PageModel model,
                    @RequestParam(value = "edit", required = false) Boolean edit,
                    @SpringBean("userService") UserService userService,
                    @SpringBean("personService") PersonService personService,
                    @SpringBean("adminService") AdministrationService administrationService) {

        User currentUser = Context.getAuthenticatedUser();

        AuthenticationUiModuleConfig config = AuthenticationUiModuleConfig.getInstance();
        PersonAttributeType phoneNumberAttributeType = getPhoneNumberAttributeType(personService, config);
        String phoneNumber = getPhoneNumber(phoneNumberAttributeType, currentUser);
        boolean twoFactorAvailable = AuthenticationConfig.getAuthenticationScheme() instanceof TwoFactorAuthenticationScheme;

        model.addAttribute("currentUser", userService.getUser(currentUser.getUserId()));
        model.addAttribute("givenName", currentUser.getPerson().getGivenName());
        model.addAttribute("familyName", currentUser.getPerson().getFamilyName());
        model.addAttribute("gender", currentUser.getPerson().getGender());
        model.addAttribute("email", currentUser.getEmail());
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("defaultLocale", getDefaultLocale(currentUser));
        model.addAttribute("allowedLocales", administrationService.getAllowedLocales());
        model.addAttribute("editMode", edit == Boolean.TRUE);
        model.addAttribute("twoFactorAvailable", twoFactorAvailable);
        model.addAttribute("twoFactorAuthenticationMethod", currentUser.getUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE));
    }

    PersonAttributeType getPhoneNumberAttributeType(PersonService personService, AuthenticationUiModuleConfig config) {
        PersonAttributeType phoneNumberAttributeType = null;
        if (config.getPhoneNumberPersonAttributeType() != null) {
            String attType = config.getPhoneNumberPersonAttributeType();
            phoneNumberAttributeType = personService.getPersonAttributeTypeByUuid(attType);
            if (phoneNumberAttributeType == null) {
                phoneNumberAttributeType = personService.getPersonAttributeTypeByName(attType);
            }
        }
        return phoneNumberAttributeType;
    }

    String getPhoneNumber(PersonAttributeType phoneNumberAttributeType, User user) {
        String phoneNumber = null;
        if (phoneNumberAttributeType != null) {
            PersonAttribute att = user.getPerson().getAttribute(phoneNumberAttributeType);
            if (att != null) {
                phoneNumber = att.getValue();
            }
        }
        return phoneNumber;
    }

    Locale getDefaultLocale(User user) {
        Locale locale = null;
        String defaultLocaleStr = user.getUserProperty("defaultLocale");
        if (StringUtils.isNotBlank(defaultLocaleStr)) {
            locale = LocaleUtility.fromSpecification(defaultLocaleStr);
        }
        if (locale == null) {
            locale = Context.getLocale();
        }
        return locale;
    }
}
