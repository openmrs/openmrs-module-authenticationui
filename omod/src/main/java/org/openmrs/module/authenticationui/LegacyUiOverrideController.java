/**
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
package org.openmrs.module.authenticationui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that overrides pages from the legacyui module that the authenticationui module replaces
 */
@Controller
public class LegacyUiOverrideController {

    @RequestMapping("admin/maintenance/currentUsers.list")
    public String activeUsers() {
        return "redirect:/authenticationui/admin/activeUsers.page";
    }

    @RequestMapping("options.form")
    public String optionsForm() {
        return "redirect:/authenticationui/account/myAccount.page";
    }

    @RequestMapping("/module/legacyui/optionsForm")
    public String optionsFormLegacyUi() {
        return "redirect:/authenticationui/account/myAccount.page";
    }

}
