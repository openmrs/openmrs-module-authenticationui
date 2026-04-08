<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authenticationui.configure2fa.title") ])
    ui.includeCss("authenticationui", "account.css", -60)
%>

<style>
    .note {
        width: 100%;
    }
    .method {
        padding-top: 10px;
        padding-left: 10px;
    }
    .preferred-method {
        padding-left: 10px;
        font-style: italic;
        font-size: smaller;
    }
    .set-preferred-link {
        font-size: smaller;
    }
    .method-action {
        padding-left: 10px;
    }
    .config-page-link {
        text-decoration: underline;
        color: #007FFF;
    }
    .available-method-section {
        padding-top: 20px;
    }
    .fa-check-circle {
        color: green;
    }
    .icon-remove-sign {
        color: red;
    }
</style>

<script type="text/javascript">
    function removeOption(schemeId) {
        const actionForm = jq("#action-form");
        actionForm.find("input[name='schemeId']").val(schemeId);
        actionForm.find("input[name='remove']").val("true");
        actionForm.submit();
    }
    function addOption(schemeId) {
        const actionForm = jq("#action-form");
        actionForm.find("input[name='schemeId']").val(schemeId);
        actionForm.find("input[name='remove']").val("");
        actionForm.submit();
    }
    function setPreferredOption(schemeId) {
        const actionForm = jq("#action-form");
        actionForm.find("input[name='schemeId']").val(schemeId);
        actionForm.find("input[name='remove']").val("");
        actionForm.find("input[name='preferred']").val("true");
        actionForm.submit();
    }
</script>

<%= ui.includeFragment("authenticationui", "accountBreadcrumbs", [ userId: user.id, label: "authenticationui.configure2fa.title" ]) %>

<h3>${ui.message("authenticationui.configure2fa.title")}</h3>
<div>
    <% if (!availableOptions.isEmpty()) { %>
        <div class="section note-container">
            <% if (!configuredSchemeIds.isEmpty()) { %>
                <div class="note success">
                    ${ui.message("authenticationui.configure2fa.accountEnabled")}
                </div>
            <% } else { %>
                <div class="note error">
                    ${ui.message("authenticationui.configure2fa.accountNotEnabled")}
                </div>
            <% } %>
        </div>
        <% if (!configuredSchemeIds.isEmpty()) { %>
            <div class="existing-method-section">
                <div class="section">
                    ${ui.message("authenticationui.configure2fa.currentlyConfiguredMethods")}
                </div>
                <div>
                    <% configuredSchemeIds.eachWithIndex { schemeId, index ->
                        def option = availableOptions.get(schemeId) %>
                        <div class="method">
                            <i class="fa fa-fw fa-check-circle"></i>
                            ${ ui.message("authenticationui." + option.schemeId + ".name") }
                            <% if (index == 0) { %>
                                <span class="preferred-method">( ${ ui.message("authenticationui.configure2fa.preferred") } )</span>
                            <% } else { %>
                                <a class="set-preferred-link" href="javascript:setPreferredOption('${option.schemeId}')">
                                    ( ${ ui.message("authenticationui.configure2fa.setPreferred") } )
                                </a>
                            <% } %>
                            <% if (option.configurationPage) { %>
                                <span class="method-action">
                                    <a href="${ option.configurationPage }">
                                        <i class="fa fa-fw fa-edit"></i>
                                    </a>
                                </span>
                            <% } %>
                            <a href="javascript:removeOption('${option.schemeId}')">
                                <i class="fa fa-fw icon-remove-sign"></i>
                            </a>
                        </div>
                    <% } %>
                </div>
            </div>
        <% } %>

        <%
                def methodsToAdd = []
                availableOptions.values().eachWithIndex { option, index ->
                    if (!configuredSchemeIds.contains(option.schemeId)) {
                        methodsToAdd.add(option)
                    }
                }
        %>
        <% if (!methodsToAdd.isEmpty()) { %>
            <div>
                <div class="section available-method-section">
                    ${ui.message("authenticationui.configure2fa.addMethod")}
                </div>
                <% availableOptions.values().eachWithIndex { option, index ->
                    def schemeId = option.schemeId
                    if (!configuredSchemeIds.contains(schemeId)) { %>
                        <form method="post" action="${ui.pageLink("authenticationui", "account/twoFactorSetup")}">
                            <div class="method">
                                ${ ui.message("authenticationui." + schemeId + ".name") }
                                <span class="add-action">
                                    <a href="javascript:addOption('${option.schemeId}')">
                                        <i class="fa fa-fw fa-plus-circle"></i>
                                    </a>
                                </span>
                            </div>
                        </form>
                    <% } %>
                <% } %>
            </div>
        <% } %>

        <form id="action-form" style="display:none; " method="post" action="${ui.pageLink("authenticationui", "account/twoFactorSetup")}">
            <input type="hidden" name="userId" value="${user.userId}"/>
            <input type="hidden" name="schemeId" value=""/>
            <input type="hidden" name="remove" value=""/>
            <input type="hidden" name="preferred" value=""/>
        </form>

    <% } else { %>
        <div class="section note-container">
            <div class="note error">
                <span class="text">${ ui.message("authenticationui.configure2fa.systemNotEnabled") }</span>
            </div>
        </div>
    <% } %>
</div>
