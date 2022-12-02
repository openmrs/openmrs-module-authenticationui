package org.openmrs.module.authenticationui.page.controller.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.authenticationui.AuthenticationUiConfig;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.ui.framework.page.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class AbstractAccountPageController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected void checkPermissionAndAddToModel(AuthenticationUiConfig authenticationUiConfig, User user, PageModel model) {
        boolean ownAccount = (user.equals(Context.getAuthenticatedUser()));
        boolean sysAdmin = Context.hasPrivilege(authenticationUiConfig.getAccountAdminPrivilege());
        if (!ownAccount && !sysAdmin) {
            throw new APIException("authenticationui.unauthorizedPageError");
        }
        model.addAttribute("user", user);
        model.addAttribute("ownAccount", ownAccount);
        model.addAttribute("sysAdmin", sysAdmin);
    }

    protected void rejectValue(Errors errors, String field, String messageCode, Object... messageArgs) {
        errors.rejectValue(field, messageCode, messageArgs, null);
    }

    protected void requireField(Errors errors, String field, String value, String fieldNameCode) {
        if (StringUtils.isBlank(value)) {
            rejectValue(errors, field, "error.required", getMessage(fieldNameCode));
        }
    }

    protected void sendErrorMessage(BindingResult errors, HttpServletRequest request) {
        List<ObjectError> allErrors = errors.getAllErrors();
        String message = "";
        for (ObjectError error : allErrors) {
            Object[] arguments = error.getArguments();
            if (error.getCode() != null) {
                String errorMessage = getMessage(error.getCode(), arguments);
                if (arguments != null) {
                    for (int i = 0; i < arguments.length; i++) {
                        String argument = (String) arguments[i];
                        errorMessage = errorMessage.replaceAll("\\{" + i + "}", argument);
                    }
                }
                message = message.concat(errorMessage.concat("<br>"));
            }
        }
        request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, message);
    }

    protected void sendErrorMessage(String code, Exception e, HttpServletRequest request) {
        log.error("An error occurred", e);
        request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, getMessage(code, e.getMessage()));
    }

    protected void setSuccessMessage(HttpServletRequest request, String code) {
        String msg = getMessage("authenticationui.account.saved");
        request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, msg);
        request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
    }

    protected String getMessage(String code, Object... arguments) {
        return Context.getMessageSourceService().getMessage(code, arguments, Context.getLocale());
    }
}
