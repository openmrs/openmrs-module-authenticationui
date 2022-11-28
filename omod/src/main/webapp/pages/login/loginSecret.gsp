<%
    ui.decorateWith("authenticationui", "standardLoginPage", [
            title: ui.message("authenticationui.loginSecret.title"),
            authenticationUiContext: authenticationUiContext
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
                <input id="answer" type="password" name="answer" placeholder="${ ui.message("authenticationui.loginSecret.secret.placeholder") }"/>
            </p>

            <p>
                <input id="login-button" class="confirm" type="submit" value="${ ui.message("authenticationui.loginSecret.button") }"/>
            </p>

        </fieldset>

    </form>

</div>
<script type="text/javascript">
    document.getElementById('answer').focus();
</script>
