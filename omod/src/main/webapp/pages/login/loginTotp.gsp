<%
    ui.decorateWith("authenticationui", "standardLoginPage", [
            title: ui.message("authenticationui.loginTotp.title"),
            authenticationUiConfig: authenticationUiConfig
    ])
%>
<div id="login-page">

    <form id="login-form" method="post" autocomplete="off">

        <h1>${ ui.message("authenticationui.loginTotp.loginInstructions") }</h1>

        <fieldset>

            <legend>
                <i class="icon-lock small"></i>
                ${ ui.message("authenticationui.loginTotp.loginHeading") }
            </legend>

            <p>
                <label for="code-input">${ ui.message("authenticationui.loginTotp.code") }</label>
                <input id="code-input" type="text" name="code" tabindex="10" value="" placeholder="${ ui.message("authenticationui.loginTotp.code.placeholder") }"/>
            </p>

            <p>
                <input id="cancel-button" class="cancel" type="button" tabindex="30" value="${ ui.message("authenticationui.login.cancel") }" onclick="document.location.href='${ui.pageLink("authenticationui", "login/login")}'" />
                <input id="login-button" class="confirm" type="submit" tabindex="20" value="${ ui.message("authenticationui.loginTotp.button") }"/>
            </p>

        </fieldset>

    </form>

</div>
<script type="text/javascript">
    document.getElementById('code-input').focus();
</script>
