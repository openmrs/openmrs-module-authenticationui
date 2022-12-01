<%
    def accountTitle = ownAccount ? ui.message("authenticationui.myAccount.title") : ui.format(user.person)
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authenticationui.changePassword.title") ])
    ui.includeCss("authenticationui", "account.css", -60)
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ accountTitle }", link: '${ui.pageLink("authenticationui", "account/userAccount", [userId: user.id])}' },
        { label: "${ ui.message("authenticationui.changePassword.title")}" }
    ];
    var errorMessageOldPassword = "${ui.message("authenticationui.changePassword.oldPassword.required")}";
    var errorMessageNewPassword = "${ui.message("authenticationui.changePassword.newPassword.required")}";
    var errorMessageNewAndConfirmPassword = "${ui.message("authenticationui.changePassword.newAndConfirmPassword.doesNotMatch")}";

    jq(function() {

        const PASSWORD_LENGTH = 8;

        disableSubmitButton();

        var timer;

        jq("#oldPassword").blur(function(){
            var oldPassword = jq(this).val();

            if (${ownAccount} && !isPasswordValid(oldPassword)) {
                jq("#oldPasswordSection .field-error").text(errorMessageOldPassword);
                jq("#oldPasswordSection .field-error").show();
                disableSubmitButton();
                return false;
            } else {
                jq("#oldPasswordSection .field-error").hide();
            }
        });

        jq("#newPassword").blur(function(){
            var newPassword = jq(this).val();

            if (!isPasswordValid(newPassword)){
                jq("#newPasswordSection .field-error").text(errorMessageNewPassword);
                jq("#newPasswordSection .field-error").show();
                disableSubmitButton();
                return false;
            } else {
                jq("#newPasswordSection .field-error").hide();
            }
        });

        jq("#confirmPassword").keyup(function(){
            if (timer) {
                clearTimeout(timer);
            }
            timer = setTimeout(confirmPasswordAction, 500);
        });

        function confirmPasswordAction() {
            var newPassword = jq("#newPassword").val();
            var confirmPassword = jq("#confirmPassword").val();

            if (confirmPassword.length >= 1 && (newPassword != confirmPassword)) {
                jq("#confirmPasswordSection .field-error").text(errorMessageNewAndConfirmPassword);
                jq("#confirmPasswordSection .field-error").show();
                disableSubmitButton();
            } else if (isPasswordValid(newPassword) && newPassword == confirmPassword) {
                jq("#confirmPasswordSection .field-error").hide();
                enableSubmitButton();
            }
        }
        function isPasswordValid(newPassword) {
            return newPassword && newPassword.length >= PASSWORD_LENGTH;
        }

        function disableSubmitButton(){
            jq("#save-button").addClass("disabled");
            jq("#save-button").attr("disabled", "disabled");
        }

        function enableSubmitButton(){
            jq("#save-button").removeClass("disabled");
            jq("#save-button").removeAttr("disabled");
        }
    });
    
</script>

<h3>${ui.message("authenticationui.changePassword.title")}</h3>

<form method="post" id="accountForm">
    <input type="hidden" name="userId" value="${user.id}"/>
    <fieldset>
        <% if (ownAccount) { %>
            <p id="oldPasswordSection" class="emr_passwordDetails">
                <label class="form-header" for="oldPassword">${ ui.message("authenticationui.changePassword.oldPassword") }</label>
                <input type="password" id="oldPassword" name="oldPassword"  autocomplete="off"/>
                ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "oldPassword" ])}
            </p>
        <% } %>
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
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='/${ contextPath }/authenticationui/account/userAccount.page?userId=${user.id}'" />
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
    </div>

</form>

