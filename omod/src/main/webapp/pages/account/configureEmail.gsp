<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authenticationui.configureEmail.title") ])
    ui.includeCss("authenticationui", "account.css", -60)
%>

<%= ui.includeFragment("authenticationui", "accountBreadcrumbs", [ userId: user.id, label: "authenticationui.configureEmail.title" ]) %>

<h3>${ui.message("authenticationui.configureEmail.title")}</h3>

<div class="section note-container">
    <div class="note warning">
        ${ codeSent
            ? ui.message("authenticationui.configureEmail.codeSentMessage", [email])
            : ui.message("authenticationui.configureEmail.configurationMessage") }
    </div>
</div>

<form method="post" id="configureEmailForm" action="${ui.pageLink("authenticationui", "account/configureEmail")}">
    <input type="hidden" name="userId" value="${user.id}"/>
    <input type="hidden" name="schemeId" value="${schemeId}"/>

    <% if (!codeSent) { %>

        <div>
            <label for="email-input">${ ui.message("authenticationui.configureEmail.email") }</label>
            <input id="email-input" type="text" name="email" value="${ ui.escapeJs(email ?: '') }" placeholder="${ ui.message("authenticationui.configureEmail.email.placeholder") }"/>
        </div>
        <div style="padding-top: 10px;">
            <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/authenticationui/account/userAccount.page?userId=${user.id}'" />
            <input type="submit" class="confirm" id="send-button" value="${ ui.message("authenticationui.configureEmail.sendCode") }"/>
        </div>

    <% } else { %>

        <div>
            <label for="code-input">${ ui.message("authenticationui.configureEmail.code") }</label>
            <input id="code-input" type="text" name="code" value="" placeholder="${ ui.message("authenticationui.configureEmail.code.placeholder") }"/>
            <input type="hidden" name="email" value="${ ui.escapeJs(email ?: '') }"/>
        </div>
        <div style="padding-top: 10px;">
            <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/authenticationui/account/userAccount.page?userId=${user.id}'" />
            <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"/>
        </div>

    <% } %>
</form>
