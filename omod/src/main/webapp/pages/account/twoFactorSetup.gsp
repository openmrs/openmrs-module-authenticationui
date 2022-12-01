<%
    def accountTitle = ownAccount ? ui.message("authenticationui.myAccount.title") : ui.format(user.person)
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authenticationui.configure2fa.title") ])
    ui.includeCss("authenticationui", "account.css", -60)
%>

<style>
    .note {
        width: 100%;
    }
    #options-choices {
        padding: 20px
    }
    .option-info {
        padding-left: 10px;
        font-style: italic;
        font-size: smaller;
    }
    .config-page-link {
        text-decoration: underline;
        color: #007FFF;
    }
</style>

<script type="text/javascript">
    var breadcrumbs = [];
    breadcrumbs.push({ icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' });
    breadcrumbs.push({ label: "${ accountTitle }", link: '${ui.pageLink("authenticationui", "account/userAccount", [userId: user.id])}' });
    breadcrumbs.push({ label: "${ ui.message("authenticationui.configure2fa.title")}" });

    jQuery(function() {
        let nextButton = jQuery("#next-button");
        nextButton.addClass("disabled").attr("disabled", "disabled");
        jQuery('input[name="schemeId"]').change(function() {
            var checkedVal = jQuery('input[name="schemeId"]:checked').val()
            if (checkedVal === '${ existingOption }') {
                nextButton.addClass("disabled").attr("disabled", "disabled");
            }
            else {
                nextButton.removeClass("disabled").removeAttr("disabled");
            }
        });
    });

</script>

<h3>${ui.message("authenticationui.configure2fa.title")}</h3>
<div>
    <% if (twoFactorAvailable) { %>
        <div class="section note-container">
            <% if (existingOption) { %>
                <div class="note success">
                    ${ui.message("authenticationui.configure2fa.accountEnabled")}
                </div>
            <% } else { %>
                <div class="note error">
                    ${ui.message("authenticationui.configure2fa.accountNotEnabled")}
                </div>
            <% } %>
        </div>
        <div>
            <div class="section" id="options-title">
                ${ui.message("authenticationui.configure2fa.changeMethod")}
            </div>
            <form id="options-form" method="post">
                <input type="hidden" name="userId" value="${user.userId}"/>
                <div id="options-choices">
                    <div class="option-choice">
                        <input id="empty-option" type="radio" name="schemeId" value="" <%= existingOption ? "" : "checked" %> />
                        <label for="empty-option">
                            ${ ui.message("authenticationui.configure2fa.noneSelected") }
                            <% if (!existingOption) { %>
                                <span class="option-info">( ${ui.message("authenticationui.configure2fa.currentlySelected")} )</span>
                            <% } %>
                        </label>
                    </div>
                    <% secondaryOptions.eachWithIndex { option, index ->
                        def schemeId = option.schemeId %>
                        <div class="option-choice">
                            <input id="option-${schemeId}" type="radio" name="schemeId" value="${schemeId}" <%= option.currentlySelected ? "checked" : "" %> />
                            <label for="option-${schemeId}">
                                ${ ui.message("authenticationui." + schemeId + ".name") }
                                <% if (option.currentlySelected) { %>
                                    <span class="option-info">( ${ui.message("authenticationui.configure2fa.currentlySelected")} )</span>
                                <% } %>
                            </label>
                        </div>
                    <% } %>
                </div>
                <div>
                    <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/authenticationui/account/userAccount.page?userId=${user.id}'" />
                    <input type="submit" class="confirm" id="next-button" value="${ ui.message("emr.next") }"  />
                </div>
            </form>
        </div>

    <% } else { %>
        <div class="section note-container">
            <div class="note error">
                <span class="text">${ ui.message("authenticationui.configure2fa.systemNotEnabled") }</span>
            </div>
        </div>
    <% } %>
</div>
