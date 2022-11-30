<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authenticationui.changePassword.title") ])
    ui.includeCss("authenticationui", "authentication.css", -50)
    ui.includeCss("authenticationui", "account.css", -60)
    ui.includeJavascript("authenticationui", "changePassword.js")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("authenticationui.myAccount.title")}", link: '${ui.pageLink("authenticationui", "account/myAccount")}' },
        { label: "${ ui.message("authenticationui.changePassword.title")}" }
    ];
    var errorMessageOldPassword = "${ui.message("authenticationui.changePassword.oldPassword.required")}";
    var errorMessageNewPassword = "${ui.message("authenticationui.changePassword.newPassword.required")}";
    var errorMessageNewAndConfirmPassword = "${ui.message("authenticationui.changePassword.newAndConfirmPassword.doesNotMatch")}";
</script>

<h3>${ui.message("authenticationui.changePassword.title")}</h3>

<form method="post" id="accountForm">
    <fieldset>
        <p id="oldPasswordSection" class="emr_passwordDetails">
            <label class="form-header" for="oldPassword">${ ui.message("authenticationui.changePassword.oldPassword") }</label>
            <input type="password" id="oldPassword" name="oldPassword"  autocomplete="off"/>
            ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "oldPassword" ])}
        </p>
        <p id="newPasswordSection" class="emr_passwordDetails">
            <label class="form-header" for="newPassword">${ ui.message("authenticationui.changePassword.newPassword") }</label>
            <input type="password" id="newPassword" name="newPassword"  autocomplete="off"/>
            <label id="format-password">${ ui.message("authenticationui.changePassword.passwordFormat") }</label>
            ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "newPassword" ])}
        </p>
        <p id="confirmPasswordSection" class="emr_passwordDetails">
            <label class="form-header" for="confirmPassword">${ ui.message("authenticationui.changePassword.confirmPassword") }</label>
            <input type="password" id="confirmPassword" name="confirmPassword"  autocomplete="off"/>
            ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "confirmPassword" ])}
        </p>
    </fieldset>

    <div>
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='/${ contextPath }/authenticationui/account/myAccount.page'" />
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
    </div>

</form>

