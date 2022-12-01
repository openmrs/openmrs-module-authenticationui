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

import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.web.bind.annotation.RequestParam;

public class AccountActionFragmentController {

    public FragmentActionResult unlock(@RequestParam("userId") User user,
                                       @SpringBean("userService") UserService userService,
                                       UiUtils ui) {

        try {
            user.removeUserProperty(OpenmrsConstants.USER_PROPERTY_LOCKOUT_TIMESTAMP);
            user.removeUserProperty(OpenmrsConstants.USER_PROPERTY_LOGIN_ATTEMPTS);
            userService.saveUser(user);
            return new SuccessResult(ui.message("authenticationui.account.unlock.success"));
        }
        catch (Exception e) {
            return new FailureResult(ui.message("authenticationui.account.unlock.fail"));
        }
    }

    public FragmentActionResult disable(@RequestParam("userId") User user,
                                       @SpringBean("userService") UserService userService,
                                       UiUtils ui) {

        try {
            userService.retireUser(user, "Disabled on account page");
            return new SuccessResult(ui.message("authenticationui.account.disable.success"));
        }
        catch (Exception e) {
            return new FailureResult(ui.message("authenticationui.account.disable.fail"));
        }
    }

    public FragmentActionResult enable(@RequestParam("userId") User user,
                                       @SpringBean("userService") UserService userService,
                                       UiUtils ui) {

        try {
            userService.unretireUser(user);
            return new SuccessResult(ui.message("authenticationui.account.enable.success"));
        }
        catch (Exception e) {
            return new FailureResult(ui.message("authenticationui.account.enable.fail"));
        }
    }

}
