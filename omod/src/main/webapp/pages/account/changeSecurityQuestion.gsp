<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authenticationui.changeSecretQuestion.title") ])
    ui.includeCss("authenticationui", "account.css", -60)
%>

<%= ui.includeFragment("authenticationui", "accountBreadcrumbs", [ userId: user.id, label: "authenticationui.changeSecretQuestion.title" ]) %>

<script type="text/javascript">

    jQuery(function() {
        let saveButton = jQuery("#save-button");
        saveButton.addClass("disabled").attr("disabled", "disabled");
        jQuery('input').keyup(function() {
            var question = jQuery("#question").val();
            var password = jQuery("#password").val();
            var answer = jQuery("#answer").val();
            var confirmAnswer = jQuery("#confirmAnswer").val();
            if (confirmAnswer.length >= 1 && (answer !== confirmAnswer)) {
                jQuery("#confirmAnswerSection .field-error").text("${ui.message("authenticationui.changePassword.confirmPassword.doesNotMatch")}").show();
            }
            else {
                jQuery("#confirmAnswerSection .field-error").text("").hide();
            }
            if (question && (password || <%= !ownAccount %>) && answer && answer === confirmAnswer) {
                saveButton.removeClass("disabled").removeAttr("disabled");
            }
            else {
                saveButton.addClass("disabled").attr("disabled", "disabled");
            }
        });
    });

</script>

<h3>${ui.message("authenticationui.changeSecretQuestion.title")}</h3>

<div class="section note-container">
    <% if (schemeId) { %>
        <div class="note warning">
            ${ui.message("authenticationui.changeSecretQuestion.configurationMessage")}
        </div>
    <% } %>
</div>

<form method="post" id="changeSecurityQuestionForm">
    <input type="hidden" name="userId" value="${user.id}"/>

    <div class="container-fluid">
        <div class="row" id="questionSection" class="emr_passwordDetails">
            <div class="col">
                <label class="form-header" for="question">${ ui.message("authenticationui.changeSecretQuestion.secretQuestion") }</label>
                <input type="text" id="question" name="question" autocomplete="off" value="${currentQuestion ? currentQuestion : ""}"/>
                ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "question" ])}
            </div>
        </div>
        <div class="row" id="answerSection" class="emr_passwordDetails">
            <div class="col">
                <label class="form-header" for="answer">${ ui.message("authenticationui.changeSecretQuestion.secretAnswer") }</label>
                <input type="password" id="answer" name="answer" autocomplete="off"/>
                ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "answer" ])}
            </div>
        </div>
        <div class="row" id="confirmAnswerSection" class="emr_passwordDetails">
            <div class="col">
                <label class="form-header" for="confirmAnswer">${ ui.message("authenticationui.changeSecretQuestion.secretAnswerConfirmation") }</label>
                <input type="password" id="confirmAnswer" name="confirmAnswer" autocomplete="off"/>
                ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "confirmAnswer" ])}
            </div>
        </div>
        <% if (ownAccount) { %>
            <div class="row" id="passwordSection" class="emr_passwordDetails">
                <div class="col">
                    <label class="form-header" for="password">${ ui.message("authenticationui.changeSecretQuestion.secretAnswerPassword") }</label>
                    <input type="password" id="password" name="password" autocomplete="off"/>
                    ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "password" ])}
                </div>
            </div>
        <% } %>
        <div class="row" style="padding-top:10px;">
            <div class="col">
                <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/authenticationui/account/userAccount.page?userId=${user.id}'" />
                <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
            </div>
        </div>
    </div>

</form>

