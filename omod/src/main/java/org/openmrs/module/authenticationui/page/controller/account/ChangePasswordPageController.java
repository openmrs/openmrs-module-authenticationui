package org.openmrs.module.authenticationui.page.controller.account;


import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public class ChangePasswordPageController extends AbstractAccountPageController {

    public ChangePassword getChangePassword(@RequestParam(value = "userId", required = false) Integer userId,
                                            @RequestParam(value = "oldPassword", required = false) String oldPassword,
                                            @RequestParam(value = "newPassword", required = false) String newPassword,
                                            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                                            @SpringBean("userService") UserService userService) {

        userId = (userId == null ? Context.getAuthenticatedUser().getUserId() : userId);
        User user = userService.getUser(userId);
        return new ChangePassword(user, oldPassword, newPassword, confirmPassword);
    }

    public String get(@MethodParam("getChangePassword") @BindParams ChangePassword changePassword,
                      PageModel model) {

        try {
            checkPermissionAndAddToModel(changePassword.getUser(), model);
        }
        catch (Exception e) {
            return "redirect:/index.htm";
        }

        return "account/changePassword";
    }

    public String post(@MethodParam("getChangePassword") @BindParams ChangePassword changePassword,
                       BindingResult errors,
                       @SpringBean("userService") UserService userService,
                       HttpServletRequest request,
                       PageModel model) {

        boolean ownAccount = (changePassword.getUser().equals(Context.getAuthenticatedUser()));

        if (ownAccount) {
            requireField(errors, "oldPassword", changePassword.getOldPassword(), "authenticationui.changePassword.oldPassword");
        }
        requireField(errors, "newPassword", changePassword.getNewPassword(), "authenticationui.changePassword.newPassword");
        requireField(errors, "confirmPassword", changePassword.getConfirmPassword(), "authenticationui.changePassword.confirmPassword");

        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            rejectValue(errors, "confirmPassword", "authenticationui.changePassword.newAndConfirmPassword.doesNotMatch");
        }

        if (!errors.hasErrors()) {
            try {
                checkPermissionAndAddToModel(changePassword.getUser(), model);
                if (ownAccount) {
                    userService.changePassword(changePassword.getOldPassword(), changePassword.getNewPassword());
                }
                else {
                    userService.changePassword(changePassword.getUser(), changePassword.getNewPassword());
                }
                setSuccessMessage(request, "authenticationui.changePassword.success");
                return "redirect:authenticationui/account/account.page?userId=" + changePassword.getUser().getId();
            }
            catch (Exception e) {
                sendErrorMessage("authenticationui.changePassword.fail", e, request);
            }
        }
        else {
            model.addAttribute("errors", errors);
            sendErrorMessage(errors, request);
        }

        return get(changePassword, model);
    }

    private static class ChangePassword {
        private final User user;
        private final String oldPassword;
        private final String newPassword;
        private final String confirmPassword;

        public ChangePassword(User user, String oldPassword, String newPassword, String confirmPassword) {
            this.user = user;
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
            this.confirmPassword = confirmPassword;
        }

        public User getUser() {
            return user;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public String getOldPassword() {
            return oldPassword;
        }
    }
}
