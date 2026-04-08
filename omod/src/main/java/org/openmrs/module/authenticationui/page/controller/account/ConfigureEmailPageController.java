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
import org.apache.commons.validator.routines.EmailValidator;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.AuthenticationScheme;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.web.AuthenticationSession;
import org.openmrs.module.authentication.web.EmailAuthenticationScheme;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authenticationui.AuthenticationUiConfig;
import org.openmrs.notification.Message;
import org.openmrs.notification.MessageException;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;

public class ConfigureEmailPageController extends AbstractAccountPageController {

    static final String SESSION_KEY_PENDING_EMAIL = "configureEmail.pendingEmail";
    static final String SESSION_KEY_CODE = "configureEmail.code";
    static final String SESSION_KEY_EXPIRY = "configureEmail.expiry";
    static final int CODE_LENGTH = 6;
    static final int CODE_EXPIRY_MINUTES = 10;

    @Override
    protected void checkPermissionAndAddToModel(AuthenticationUiConfig authenticationUiConfig, User user, PageModel model) {
        super.checkPermissionAndAddToModel(authenticationUiConfig, user, model);
        boolean ownAccount = (user.equals(Context.getAuthenticatedUser()));
        if (!ownAccount && !Context.hasPrivilege(PrivilegeConstants.EDIT_USER_PASSWORDS)) {
            throw new APIException("authenticationui.unauthorizedPageError");
        }
    }

    public String get(PageModel model,
                      @RequestParam(value = "userId", required = false) String userId,
                      @RequestParam(value = "schemeId", required = false) String schemeId,
                      @SpringBean("userService") UserService userService,
                      @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig,
                      HttpSession session) {

        User user = getUserOrAuthenticatedUser(userService, userId);
        try {
            checkPermissionAndAddToModel(authenticationUiConfig, user, model);
        }
        catch (Exception e) {
            return "redirect:/index.htm";
        }

        String pendingEmail = (String) session.getAttribute(SESSION_KEY_PENDING_EMAIL);

        model.addAttribute("schemeId", schemeId);
        model.addAttribute("email", pendingEmail != null ? pendingEmail : StringUtils.defaultString(user.getEmail()));
        model.addAttribute("codeSent", pendingEmail != null);

        return "account/configureEmail";
    }

    public String post(@RequestParam(value = "userId", required = false) String userId,
                       @RequestParam(value = "schemeId", required = false) String schemeId,
                       @RequestParam(value = "email", required = false) String email,
                       @RequestParam(value = "code", required = false) String code,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig,
                       HttpServletRequest request,
                       HttpSession session,
                       PageModel model) {

        User user = getUserOrAuthenticatedUser(userService, userId);
        boolean ownAccount = user.equals(Context.getAuthenticatedUser());
        AuthenticationSession authenticationSession = new AuthenticationSession(session);
        EmailAuthenticationScheme scheme = (EmailAuthenticationScheme) AuthenticationConfig.getAuthenticationScheme(schemeId);

        try {
            checkPermissionAndAddToModel(authenticationUiConfig, user, model);

            if (StringUtils.isBlank(code)) {
                // Step 1: validate email and send verification code
                if (!EmailValidator.getInstance().isValid(email)) {
                    throw new RuntimeException(getMessage("authenticationui.configureEmail.email.invalid"));
                }
                String generatedCode = generateCode();
                sendCode(email, generatedCode);

                long expiry = System.currentTimeMillis() + (CODE_EXPIRY_MINUTES * 60_000L);
                session.setAttribute(SESSION_KEY_PENDING_EMAIL, email);
                session.setAttribute(SESSION_KEY_CODE, generatedCode);
                session.setAttribute(SESSION_KEY_EXPIRY, expiry);

                String redirectUrl = "authenticationui/account/configureEmail.page";
                if (StringUtils.isNotBlank(userId)) {
                    redirectUrl += "?userId=" + user.getUserId();
                    if (StringUtils.isNotBlank(schemeId)) {
                        redirectUrl += "&schemeId=" + schemeId;
                    }
                } else if (StringUtils.isNotBlank(schemeId)) {
                    redirectUrl += "?schemeId=" + schemeId;
                }
                return "redirect:" + redirectUrl;
            }
            else {
                // Step 2: verify submitted code
                String expectedCode = (String) session.getAttribute(SESSION_KEY_CODE);
                String pendingEmail = (String) session.getAttribute(SESSION_KEY_PENDING_EMAIL);
                Long expiry = (Long) session.getAttribute(SESSION_KEY_EXPIRY);

                if (expectedCode == null || pendingEmail == null) {
                    throw new RuntimeException(getMessage("authenticationui.configureEmail.code.notSent"));
                }
                if (expiry == null || System.currentTimeMillis() > expiry) {
                    session.removeAttribute(SESSION_KEY_CODE);
                    session.removeAttribute(SESSION_KEY_PENDING_EMAIL);
                    session.removeAttribute(SESSION_KEY_EXPIRY);
                    throw new RuntimeException(getMessage("authenticationui.configureEmail.code.expired"));
                }
                if (!expectedCode.equals(code.trim())) {
                    throw new RuntimeException(getMessage("authenticationui.configureEmail.code.invalid"));
                }

                // Code is valid — update the user
                if (StringUtils.isBlank(user.getEmail())) {
                    user.setEmail(pendingEmail);
                }
                user.setUserProperty(scheme.getVerifiedEmailUserPropertyName(), pendingEmail);
                if (StringUtils.isNotBlank(schemeId)) {
                    AuthenticationScheme authenticationScheme = AuthenticationConfig.getAuthenticationScheme();
                    if (authenticationScheme instanceof TwoFactorAuthenticationScheme) {
                        ((TwoFactorAuthenticationScheme) authenticationScheme).addSecondaryAuthenticationSchemeForUser(user, schemeId);
                    }
                }
                userService.saveUser(user);

                session.removeAttribute(SESSION_KEY_PENDING_EMAIL);
                session.removeAttribute(SESSION_KEY_CODE);
                session.removeAttribute(SESSION_KEY_EXPIRY);

                if (ownAccount) {
                    authenticationSession.refreshAuthenticatedUser();
                }
                setSuccessMessage(request, "authenticationui.configureEmail.success");
                return "redirect:authenticationui/account/twoFactorSetup.page?userId=" + user.getId();
            }
        }
        catch (Exception e) {
            sendErrorMessage("authenticationui.configureEmail.fail", e, request);
        }

        return get(model, userId, schemeId, userService, authenticationUiConfig, session);
    }

    protected String generateCode() {
        int max = (int) Math.pow(10, CODE_LENGTH);
        int code = new SecureRandom().nextInt(max);
        return String.format("%0" + CODE_LENGTH + "d", code);
    }

    protected void sendCode(String email, String code) {
        try {
            MessageSource messageSource = Context.getMessageSourceService().getActiveMessageSource();
            String subject = messageSource.getMessage("authentication.email.subject", new Object[] {code}, Context.getLocale());
            Message message = Context.getMessageService().createMessage(email, "", subject, subject);
            Context.getMessageService().sendMessage(message);
        }
        catch (MessageException e) {
            throw new RuntimeException(getMessage("authenticationui.configureEmail.sendFailed"));
        }
    }
}
