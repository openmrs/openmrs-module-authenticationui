<%
    ui.decorateWith("authenticationui", "standardLoginPage", [
            title: ui.message("authenticationui.loginTotp.title"),
            authenticationUiContext: authenticationUiContext
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
                <input id="code-input" type="text" name="code" value="" placeholder="${ ui.message("authenticationui.loginTotp.code.placeholder") }"/>
            </p>

            <p>
                <input id="login-button" class="confirm" type="submit" value="${ ui.message("authenticationui.loginTotp.button") }"/>
            </p>

        </fieldset>

    </form>

</div>
<script type="text/javascript">
    document.getElementById('code-input').focus();
</script>
