<%
    ui.includeFragment("appui", "standardEmrIncludes")
    ui.includeCss("authenticationui", "account.css")
%>

<!DOCTYPE html>
<html>
<head>
    <title></title>
    <link rel="shortcut icon" type="image/ico" href="/${ ui.contextPath() }/images/openmrs-favicon.ico"/>
    <link rel="icon" type="image/png\" href="/${ ui.contextPath() }/images/openmrs-favicon.png"/>
    ${ ui.resourceLinks() }
    <script src="/${ui.contextPath()}/csrfguard" type="text/javascript"></script>
</head>
<body>
<script type="text/javascript">
    var OPENMRS_CONTEXT_PATH = '${ ui.contextPath() }';
    var errorMessageNewPassword = "${ui.message("authenticationui.changePassword.newPassword.required")}";
    var errorMessageConfirmPassword = "${ui.message("authenticationui.changePassword.confirmPassword.required")}";
    var errorMessageNewAndConfirmPassword = "${ui.message("authenticationui.changePassword.confirmPassword.doesNotMatch")}";

    jq(function() {

        disableSubmitButton();

        var timer;

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

            if (confirmPassword.length === 0) {
                jq("#confirmPasswordSection .field-error").text(errorMessageConfirmPassword);
                jq("#confirmPasswordSection .field-error").show();
                disableSubmitButton();
            }
            else if (confirmPassword.length >= 1 && (newPassword !== confirmPassword)) {
                jq("#confirmPasswordSection .field-error").text(errorMessageNewAndConfirmPassword);
                jq("#confirmPasswordSection .field-error").show();
                disableSubmitButton();
            } else if (isPasswordValid(newPassword) && newPassword === confirmPassword) {
                jq("#confirmPasswordSection .field-error").hide();
                enableSubmitButton();
            }
        }

        function isPasswordValid(pw) {
            return pw && pw.length >= 0;
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

${ ui.includeFragment("appui", "header") }

<h3>${ui.message("authenticationui.login.resetPassword")}</h3>

<div id="body-wrapper" class="container">
    <div id="reset-password-page">
        <form method="post" id="reset-password-forms">
            <fieldset>

                <legend>${ ui.message("authenticationui.changePassword.newPassword") }</legend>

                <p id="newPasswordSection" class="emr_passwordDetails">
                    <label class="form-header" for="newPassword">${ ui.message("authenticationui.changePassword.newPassword") }</label>
                    <input type="password" id="newPassword" name="newPassword"  autocomplete="off"/>
                    ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "newPassword" ])}
                </p>

                <p id="confirmPasswordSection" class="emr_passwordDetails">
                    <label class="form-header" for="confirmPassword">${ ui.message("authenticationui.changePassword.confirmPassword") }</label>
                    <input type="password" id="confirmPassword" name="confirmPassword"  autocomplete="off"/>
                    ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "confirmPassword" ])}
                </p>

            </fieldset>

            <div>
                <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='/${ contextPath }/index.htm'" />
                <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
            </div>

        </form>
    </div>
</div>
