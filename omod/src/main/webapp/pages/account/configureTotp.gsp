<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authenticationui.configureTotp.title") ])
    ui.includeCss("authenticationui", "authentication.css", -50)
    ui.includeCss("authenticationui", "account.css", -60)
    def returnUrl = isOwnAccount ? "myAccount.page" : "account.page?personId=" + userToSetup.person.personId;
%>

<script type="text/javascript">
    var breadcrumbs = [];
    breadcrumbs.push({ icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' });
    <% if (isOwnAccount) { %>
        breadcrumbs.push({ label: "${ ui.message("authenticationui.myAccount.title")}", link: '${ui.pageLink("authenticationui", "account/myAccount")}' });
    <% } else { %>
        breadcrumbs.push({ label: "${ ui.message("authenticationui.systemAdministration.title")}", link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}' });
        breadcrumbs.push({ label: "${ ui.message("authenticationui.manageAccounts.title")}" , link: '${ui.pageLink("authenticationui", "admin/manageAccounts")}'});
        breadcrumbs.push({ label: "${ ui.format(userToSetup.person) }", link: '${ui.pageLink("authenticationui", "account/account", [personId: userToSetup.person.personId])}' });
    <% } %>
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
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/authenticationui/account/${returnUrl}'" />
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
    </div>
</form>

