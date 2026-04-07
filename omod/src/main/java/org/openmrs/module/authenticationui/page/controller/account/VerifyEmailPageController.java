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

package org.openmrs.module.authenticationui.page.controller.account;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.web.EmailAuthenticationScheme;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import static org.openmrs.util.PrivilegeConstants.EDIT_USERS;
import static org.openmrs.util.PrivilegeConstants.GET_USERS;

public class VerifyEmailPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public void get(@RequestParam(value = "userId", required = false) String userId,
                    @RequestParam(value = "token", required = false) String token,
                    @SpringBean("userService") UserService userService,
                    PageModel model) {

        boolean success = false;
        String errorCode = null;

        try {
            Context.addProxyPrivilege(GET_USERS);
            Context.addProxyPrivilege(EDIT_USERS);

            if (StringUtils.isBlank(userId) || StringUtils.isBlank(token)) {
                errorCode = "authenticationui.verifyEmail.error.invalidToken";
            } else {
                User user = null;
                try {
                    user = userService.getUser(Integer.parseInt(userId));
                } catch (NumberFormatException e) {
                    user = userService.getUserByUuid(userId);
                }

                if (user == null) {
                    errorCode = "authenticationui.verifyEmail.error.invalidToken";
                } else {
                    String storedToken = user.getUserProperty(EmailAuthenticationScheme.USER_PROPERTY_VERIFICATION_TOKEN);
                    String storedExpiryStr = user.getUserProperty(EmailAuthenticationScheme.USER_PROPERTY_VERIFICATION_TOKEN_EXPIRY);

                    if (StringUtils.isBlank(storedToken) || !storedToken.equals(token)) {
                        errorCode = "authenticationui.verifyEmail.error.invalidToken";
                    } else if (StringUtils.isBlank(storedExpiryStr) || System.currentTimeMillis() > Long.parseLong(storedExpiryStr)) {
                        errorCode = "authenticationui.verifyEmail.error.expiredToken";
                    } else {
                        user.setUserProperty(EmailAuthenticationScheme.USER_PROPERTY_VERIFIED_EMAIL, user.getEmail());
                        user.removeUserProperty(EmailAuthenticationScheme.USER_PROPERTY_VERIFICATION_TOKEN);
                        user.removeUserProperty(EmailAuthenticationScheme.USER_PROPERTY_VERIFICATION_TOKEN_EXPIRY);
                        userService.saveUser(user);
                        success = true;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error verifying email address", e);
            errorCode = "authenticationui.verifyEmail.error.invalidToken";
        } finally {
            Context.removeProxyPrivilege(GET_USERS);
            Context.removeProxyPrivilege(EDIT_USERS);
        }

        model.addAttribute("success", success);
        model.addAttribute("errorCode", errorCode);
    }
}
