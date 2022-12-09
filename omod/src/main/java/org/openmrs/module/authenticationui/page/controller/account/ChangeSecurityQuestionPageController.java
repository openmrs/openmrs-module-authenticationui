package org.openmrs.module.authenticationui.page.controller.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authenticationui.AuthenticationUiConfig;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public class ChangeSecurityQuestionPageController extends AbstractAccountPageController {

    public ChangeSecurityQuestion getChangeSecurityQuestion(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "question", required = false) String question,
            @RequestParam(value = "answer", required = false) String answer,
            @RequestParam(value = "confirmAnswer", required = false) String confirmAnswer,
            @SpringBean("userService") UserService userService) {

        userId = (userId == null ? Context.getAuthenticatedUser().getUserId() : userId);
        User user = userService.getUser(userId);
        return new ChangeSecurityQuestion(user, password, question, answer, confirmAnswer);
    }

    @Override
    protected void checkPermissionAndAddToModel(AuthenticationUiConfig authenticationUiConfig, User user, PageModel model) {
        super.checkPermissionAndAddToModel(authenticationUiConfig, user, model);
        boolean ownAccount = (user.equals(Context.getAuthenticatedUser()));
        if (!ownAccount && !Context.hasPrivilege(PrivilegeConstants.EDIT_USER_PASSWORDS)) {
            throw new APIException("authenticationui.unauthorizedPageError");
        }
    }

    public String get(PageModel model,
                      @MethodParam("getChangeSecurityQuestion") @BindParams ChangeSecurityQuestion securityQuestion,
                      @RequestParam(value = "schemeId", required = false) String schemeId,
                      @SpringBean("userService") UserService userService,
                      @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig) {

        try {
            checkPermissionAndAddToModel(authenticationUiConfig, securityQuestion.getUser(), model);
        }
        catch (Exception e) {
            return "redirect:/index.htm";
        }

        model.addAttribute("schemeId", schemeId);
        model.addAttribute("currentQuestion", userService.getSecretQuestion(securityQuestion.getUser()));
        return "account/changeSecurityQuestion";
    }

    public String post(@MethodParam("getChangeSecurityQuestion") @BindParams ChangeSecurityQuestion securityQuestion,
                       BindingResult errors,
                       @RequestParam(value = "schemeId", required = false) String schemeId,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig,
                       HttpServletRequest request,
                       PageModel model) {

        boolean ownAccount = (securityQuestion.getUser().equals(Context.getAuthenticatedUser()));

        // Validate submission
        if (ownAccount) {
            requireField(errors, "password", securityQuestion.getPassword(), "authenticationui.changeSecretQuestion.password");
        }
        requireField(errors, "answer", securityQuestion.getAnswer(), "authenticationui.changeSecretQuestion.secretAnswer");
        requireField(errors, "confirmAnswer", securityQuestion.getConfirmAnswer(), "authenticationui.changeSecretQuestion.secretAnswerConfirmation");
        if (!securityQuestion.getAnswer().equalsIgnoreCase(securityQuestion.getConfirmAnswer())) {
            rejectValue(errors, "confirmAnswer", "authenticationui.changeSecretQuestion.secretAnswerConfirmation.noMatch");
        }

        if (!errors.hasErrors()) {
            try {
                checkPermissionAndAddToModel(authenticationUiConfig, securityQuestion.getUser(), model);
                if (ownAccount) {
                    userService.changeQuestionAnswer(securityQuestion.getPassword(), securityQuestion.getQuestion(), securityQuestion.getAnswer());
                }
                else {
                    userService.changeQuestionAnswer(securityQuestion.getUser(), securityQuestion.getQuestion(), securityQuestion.getAnswer());
                }
                if (StringUtils.isNotBlank(schemeId)) {
                    securityQuestion.getUser().setUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE, schemeId);
                    userService.saveUser(securityQuestion.getUser());
                }
                setSuccessMessage(request, "authenticationui.changeSecretQuestion.success");
                return "redirect:authenticationui/account/userAccount.page?userId=" + securityQuestion.getUser().getId();
            }
            catch (Exception e) {
                if ("Passwords don't match".equals(e.getMessage())) {
                    e = new RuntimeException("authentication.error.invalidPassword", e);
                }
                sendErrorMessage("authenticationui.changeSecretQuestion.fail", e, request);
            }
        }
        else {
            sendErrorMessage(errors, request);
        }

        return get(model, securityQuestion, schemeId, userService, authenticationUiConfig);
    }

    private static class ChangeSecurityQuestion {

        private final User user;
        private final String password;
        private final String question;
        private final String answer;
        private final String confirmAnswer;

        public ChangeSecurityQuestion(User user, String password, String question, String answer, String confirmAnswer) {
            this.user = user;
            this.password = password;
            this.question = question;
            this.answer = answer;
            this.confirmAnswer = confirmAnswer;
        }

        public User getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }

        public String getConfirmAnswer() {
            return confirmAnswer;
        }
    }
}
