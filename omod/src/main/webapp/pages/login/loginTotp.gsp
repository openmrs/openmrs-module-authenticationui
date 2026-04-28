<%
    ui.decorateWith("authenticationui", "standardLoginPage", [
            title: ui.message("authenticationui.loginTotp.title"),
            authenticationUiConfig: authenticationUiConfig
    ])
%>
<style>
    #login-form ul.select {
        width: unset;
    }
    #login-form input[type=text], #login-form input[type=password] {
        min-width: 100%;
    }
    #login-page fieldset {
        display: block;
    }
</style>
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

            <% if (authenticationUiConfig.isShowRememberMe()) { %>
            <p>
                <label for="rememberMe-checkbox">
                    <input id="rememberMe-checkbox" type="checkbox" name="rememberMe" value="true" tabindex="15"/>
                    ${ ui.message("authenticationui.login.rememberMe") }
                </label>
            </p>
            <% } %>

            <p>
                <input id="cancel-button" class="cancel" type="button" tabindex="30" value="${ ui.message("authenticationui.login.cancel") }" onclick="document.location.href='${ui.pageLink("authenticationui", "login/login")}'" />
                <input id="login-button" class="confirm" type="submit" tabindex="20" value="${ ui.message("authenticationui.loginTotp.button") }"/>
            </p>

        </fieldset>

        <%= ui.includeFragment("authenticationui", "twoFactorAlternatives") %>

    </form>

</div>
<script type="text/javascript">
    document.getElementById('code-input').focus();
</script>
