<%
    def accountTitle = ownAccount ? ui.message("authenticationui.myAccount.title") : ui.format(user.person)
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authenticationui.configureTotp.title") ])
    ui.includeCss("authenticationui", "account.css", -60)
%>

<script type="text/javascript">
    var breadcrumbs = [];
    breadcrumbs.push({ icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' });
    breadcrumbs.push({ label: "${ accountTitle }", link: '${ui.pageLink("authenticationui", "account/account", [userId: user.id])}' });
    breadcrumbs.push({ label: "${ ui.message("authenticationui.configureTotp.title")}" });
</script>

<h3>${ui.message("authenticationui.configureTotp.title")}</h3>

<div class="section note-container">
    <% if (schemeId) { %>
        <div class="note warning">
            ${ui.message("authenticationui.configureTotp.configurationMessage")}
        </div>
    <% } %>
</div>

<form method="post" id="totpVerificationForm">
    <input type="hidden" name="userId" value="${user.id}"/>
    <fieldset>
        <div>
            <img src="${qrCodeUri}" />
        </div>
        <div>
            <label for="code-input">${ ui.message("authenticationui.configureTotp.code") }</label>
            <input type="hidden" name="secret" value="${secret}"/>
            <input id="code-input" type="text" name="code" value="" placeholder="${ ui.message("authenticationui.configureTotp.code.placeholder") }"/>
        </div>
    </fieldset>
    <div>
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/authenticationui/account/account.page?userId=${user.id}'" />
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
    </div>
</form>

