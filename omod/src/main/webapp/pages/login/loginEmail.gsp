<%
    ui.decorateWith("authenticationui", "standardLoginPage", [
            title: ui.message("authenticationui.loginEmail.title"),
            authenticationUiConfig: authenticationUiConfig
    ])
%>
<div id="login-page">

    <form id="login-form" method="post" autocomplete="off">

        <h1>${ ui.message("authenticationui.loginEmail.loginInstructions") }</h1>

        <fieldset>

            <legend>
                <i class="icon-lock small"></i>
                ${ ui.message("authenticationui.loginEmail.loginHeading") }
            </legend>

            <p>${ ui.message("authenticationui.loginEmail.codeSentTo", [email]) }</p>

            <p>
                <label for="code-input">${ ui.message("authenticationui.loginEmail.code") }</label>
                <input id="code-input" type="text" name="code" tabindex="10" value="" placeholder="${ ui.message("authenticationui.loginEmail.code.placeholder") }"/>
            </p>

            <p>
                <input id="cancel-button" class="cancel" type="button" tabindex="40" value="${ ui.message("authenticationui.login.cancel") }" onclick="document.location.href='${ui.pageLink("authenticationui", "login/login")}'" />
                <input id="resend-button" class="cancel" type="submit" tabindex="30" name="resend" value="${ ui.message("authenticationui.loginEmail.resend") }"/>
                <input id="login-button" class="confirm" type="submit" tabindex="20" value="${ ui.message("authenticationui.loginEmail.button") }"/>
            </p>

        </fieldset>

    </form>

</div>
<script type="text/javascript">
    document.getElementById('code-input').focus();
</script>
