<script type="text/javascript">
    function changeSecondFactor(schemeId) {
        const actionLink = '${ui.actionLink("authenticationui", "twoFactorAlternatives", "changeSecondaryAuthenticationSchemeForSession")}';
        jq.getJSON(actionLink, { schemeId: schemeId }).success(function(data) {
            document.location.href = '${homePageUrl}';
        }).error(function(xhr) {
            console.error(xhr);
        });
    }
</script>
<div class="row">
    <span class="col text-right">
        ${ ui.message("authenticationui.login2fa.chooseSecondFactor") }<br/>
        <% schemeIds.each { schemeId -> %>
            <a href="javascript:changeSecondFactor('${schemeId}')">${ ui.message("authenticationui." + schemeId + ".name") }</a>
            <br/>
        <% } %>
    </span>
</div>
