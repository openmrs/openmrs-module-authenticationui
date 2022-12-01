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
import org.apache.commons.validator.routines.EmailValidator;
import org.openmrs.Location;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authenticationui.AuthenticationUiModuleConfig;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.LocaleUtility;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class UserAccountPageController extends AbstractAccountPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public Account getAccount(@RequestParam(value = "userId", required = false) Integer userId,
                              @SpringBean("userService") UserService userService,
                              @SpringBean("personService") PersonService personService,
                              @SpringBean("locationService") LocationService locationService) {

        userId = (userId == null ? Context.getAuthenticatedUser().getUserId() : userId);
        User user = userService.getUser(userId);
        return new Account(user, personService, locationService);
    }

    public String get(PageModel model,
                    @MethodParam("getAccount") @BindParams Account account,
                    @RequestParam(value = "edit", required = false) Boolean edit,
                    @SpringBean("locationService") LocationService locationService,
                    @SpringBean("adminService") AdministrationService administrationService) {

        try {
            checkPermissionAndAddToModel(account.getUser(), model);
        }
        catch (Exception e) {
            return "redirect:/index.htm";
        }

        boolean twoFactorAvailable = AuthenticationConfig.getAuthenticationScheme() instanceof TwoFactorAuthenticationScheme;

        model.addAttribute("account", account);
        model.addAttribute("allowedLocales", administrationService.getAllowedLocales());
        model.addAttribute("editMode", edit == Boolean.TRUE);
        model.addAttribute("twoFactorAvailable", twoFactorAvailable);
        model.addAttribute("locations", locationService.getAllLocations());
        return "account/userAccount";
    }

    public String post(@MethodParam("getAccount") @BindParams Account account, BindingResult errors,
                       @SpringBean("messageSource") MessageSource messageSource,
                       @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                       @SpringBean("locationService") LocationService locationService,
                       @SpringBean("adminService") AdministrationService administrationService,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("personService") PersonService personService,
                       PageModel model,
                       HttpServletRequest request) {

        requireField(errors, "givenName", account.getGivenName(), "authenticationui.account.givenName");
        requireField(errors, "familyName", account.getFamilyName(), "authenticationui.account.familyName");
        requireField(errors, "gender", account.getGender(), "authenticationui.account.gender");

        if (StringUtils.isNotBlank(account.getEmail())) {
            if (!EmailValidator.getInstance().isValid(account.getEmail())) {
                errors.rejectValue("email", "error.email.invalid");
            }
            else {
                User existingUser = userService.getUserByUsernameOrEmail(account.getEmail());
                if (existingUser != null && !existingUser.equals(account.getUser())) {
                    if (account.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
                        errors.rejectValue("email", "authenticationui.account.error.emailAlreadyInUse");
                    }
                }
            }
        }

        if (!errors.hasErrors()) {
            try {
                checkPermissionAndAddToModel(account.getUser(), model);

                userService.saveUser(account.getUser());
                Context.refreshAuthenticatedUser();
                if (account.getDefaultLocale() != null) {
                    Context.setLocale(account.getDefaultLocale());
                }

                setSuccessMessage(request, "authenticationui.account.saved");
                return "redirect:authenticationui/account/userAccount.page?userId=" + account.getUser().getId();
            }
            catch (Exception e) {
                log.warn("Some error occurred while saving account details:", e);
                sendErrorMessage("authenticationui.account.error.save.fail", e, request);
            }
        }
        else {
            sendErrorMessage(errors, request);
        }

        // reload page on error
        return get(model, account, true, locationService, administrationService);
    }

    public static class Account {

        private final User user;
        private final PersonService personService;
        private final LocationService locationService;

        public Account(User user, PersonService personService, LocationService locationService) {
            this.user = user;
            this.personService = personService;
            this.locationService = locationService;
        }

        public String getGivenName() {
            return user.getGivenName();
        }

        public void setGivenName(String givenName) {
            if (user.getPerson().getPersonName() == null) {
                user.getPerson().addName(new PersonName());
            }
            user.getPerson().getPersonName().setGivenName(givenName);
        }

        public String getFamilyName() {
            return user.getFamilyName();
        }

        public void setFamilyName(String familyName) {
            if (user.getPerson().getPersonName() == null) {
                user.getPerson().addName(new PersonName());
            }
            user.getPerson().getPersonName().setFamilyName(familyName);
        }

        public String getGender() {
            return user.getPerson().getGender();
        }

        public void setGender(String gender) {
            user.getPerson().setGender(gender);
        }

        public String getEmail() {
            return user.getEmail();
        }

        public void setEmail(String email) {
            user.setEmail(email);
        }

        public String getPhoneNumber() {
            PersonAttributeType phoneNumberAttributeType = getPhoneNumberAttributeType();
            String phoneNumber = null;
            if (phoneNumberAttributeType != null) {
                PersonAttribute att = user.getPerson().getAttribute(phoneNumberAttributeType);
                if (att != null) {
                    phoneNumber = att.getValue();
                }
            }
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            PersonAttributeType phoneNumberAttributeType = getPhoneNumberAttributeType();
            if (phoneNumberAttributeType != null) {
                user.getPerson().addAttribute(new PersonAttribute(phoneNumberAttributeType, phoneNumber));
            }
        }

        public Locale getDefaultLocale() {
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

        public void setDefaultLocale(Locale defaultLocale) {
            if (defaultLocale != null) {
                user.setUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE, defaultLocale.toString());
            }
            else {
                user.removeUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE);
            }
        }

        public String getDefaultLocationId() {
            if (StringUtils.isNotBlank(getDefaultLocationUserProperty())) {
                return user.getUserProperty(getDefaultLocationUserProperty());
            }
            return null;
        }

        public String getDefaultLocationName() {
            if (StringUtils.isNotBlank(getDefaultLocationId())) {
                Location location = locationService.getLocation(Integer.parseInt(getDefaultLocationId()));
                if (location != null) {
                    return location.getName();
                }
            }
            return null;
        }

        public void setDefaultLocationId(String defaultLocation) {
            if (StringUtils.isNotBlank(getDefaultLocationUserProperty())) {
                if (StringUtils.isNotBlank(defaultLocation)) {
                    user.setUserProperty(getDefaultLocationUserProperty(), defaultLocation);
                }
                else {
                    user.removeUserProperty(getDefaultLocationUserProperty());
                }
            }
        }

        public String getTwoFactorAuthenticationMethod() {
            return user.getUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE);
        }

        public PersonAttributeType getPhoneNumberAttributeType() {
            PersonAttributeType phoneNumberAttributeType = null;
            String attType = AuthenticationUiModuleConfig.getInstance().getPhoneNumberPersonAttributeType();
            if (StringUtils.isNotBlank(attType)) {
                phoneNumberAttributeType = personService.getPersonAttributeTypeByUuid(attType);
                if (phoneNumberAttributeType == null) {
                    phoneNumberAttributeType = personService.getPersonAttributeTypeByName(attType);
                }
            }
            return phoneNumberAttributeType;
        }

        public String getDefaultLocationUserProperty() {
            return AuthenticationUiModuleConfig.getInstance().getDefaultLocationUserProperty();
        }

        public boolean isEnabled() {
            return !user.isRetired();
        }

        public boolean isLocked() {
            String lockoutTimeProperty = user.getUserProperty(OpenmrsConstants.USER_PROPERTY_LOCKOUT_TIMESTAMP);
            if (lockoutTimeProperty != null) {
                try {
                    long lockedOutUntil = Long.parseLong(lockoutTimeProperty) + 300000;
                    return System.currentTimeMillis() < lockedOutUntil;
                } catch (NumberFormatException ex) {
                    return false;
                }
            }
            return false;
        }

        public User getUser() {
            return user;
        }
    }
}
