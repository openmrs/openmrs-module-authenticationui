<%
    ui.decorateWith("authenticationui", "standardLoginPage", [
            title: ui.message("authenticationui.loginSecret.title"),
            authenticationUiConfig: authenticationUiConfig
    ])
%>
<div id="login-page">

    <form id="login-form" method="post" autocomplete="off">

        <h1>${ ui.message("authenticationui.loginSecret.loginInstructions") }</h1>

        <fieldset>

            <legend>
                <i class="icon-lock small"></i>
                ${ ui.message("authenticationui.loginSecret.loginHeading") }
            </legend>

            <p>
                <label for="answer">
                    <input type="hidden" name="question" value="${question}"/>
                    ${ ui.message(question) }:
                </label>
                <input id="answer" type="password" name="answer" tabindex="10" placeholder="${ ui.message("authenticationui.loginSecret.secret.placeholder") }"/>
            </p>

            <p>
                <input id="cancel-button" class="cancel" type="button" tabindex="30" value="${ ui.message("authenticationui.login.cancel") }" onclick="document.location.href='${ui.pageLink("authenticationui", "login/login")}'" />
                <input id="login-button" class="confirm" type="submit" tabindex="20" value="${ ui.message("authenticationui.loginSecret.button") }"/>
            </p>

        </fieldset>

    </form>

</div>
<script type="text/javascript">
    document.getElementById('answer').focus();
</script>
