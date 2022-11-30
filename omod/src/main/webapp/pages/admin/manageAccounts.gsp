<%
    ui.decorateWith("appui", "standardEmrPage", [title: ui.message("authenticationui.manageAccounts.title")])
	ui.includeCss("authenticationui", "authentication.css", -50)
	ui.includeCss("authenticationui", "account.css")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("authenticationui.systemAdministration.title")}", link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}' },
        { label: "${ ui.message("authenticationui.manageAccounts.title")}" }
    ];
</script>

<h3>${  ui.message("authenticationui.manageAccounts.title") }</h3>

<div style="display:flex; justify-content: space-between;">
	<div>
		<a href="${ ui.pageLink("authenticationui", "account/account") }">
			<button id="create-account-button">${ ui.message("emr.createAccount") }</button>
		</a>
	</div>
	<div style="margin-left:auto;">
		<input type="checkbox" id="filter-only-enabled" value="true" />
		${ ui.message("emr.account.showOnlyEnabled.label") }
	</div>
</div>

<hr>
<table id="list-accounts" cellspacing="0" cellpadding="2">
	<thead>
		<tr>
			<th>${ ui.message("emr.person.name")}</th>
			<th>${ ui.message("emr.user.username") }</th>
			<th>${ ui.message("emr.gender") }</th>
            <th>${ ui.message("emr.account.providerRole.label") }</th>
            <th>${ ui.message("emr.account.providerIdentifier.label") }</th>
            <th>${ ui.message("emr.account.enabled.label") }</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<% accounts.sort { (it.person.personName !=null && it.person.personName.familyName != null) ? it.person.personName.familyName.toLowerCase() : false }.each{  %>
	 	<tr>
	 		<td>
				${ ui.format(it.person.personName)}
			</td>
			<td>
				<% if(it.username && it.username != '') {%>
					${ ui.format(it.username) }
				<% } %>
			</td>
			<td>
				${ ui.format(it.person.gender) }
			</td>
            <td>
                ${ ui.format(it.providerRole) }
            </td>
            <td>
                ${ ui.format(it.provider?.identifier) }
            </td>
            <td>
                ${ it.userEnabled ? ui.message("emr.yes") : ui.message("emr.no") }
            </td>
			<td>
	            <a href="/${ contextPath }/authenticationui/account/account.page?personId=${ it.person.personId }">
	                <button>${ ui.message("general.view") }</button>
	            </a>
        	</td>
		</tr>
		<% } %>
	</tbody>
</table>

<% if ( (accounts != null) && (accounts.size() > 0) ) { %>
${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#list-accounts",
        options: [
                bFilter: true,
                bJQueryUI: true,
                bLengthChange: false,
                iDisplayLength: 10,
                sPaginationType: '\"full_numbers\"',
                bSort: false,
                sDom: '\'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>\''
        ]
]) }
<% } %>
<script type="text/javascript">
	jq( document ).ready(function() {
		jq("#filter-only-enabled").click(function(event) {
			let table = jq("#list-accounts").dataTable();
			let onlyEnabled = jq(this).is(':checked');
			if (onlyEnabled) {
				table.fnFilter('${ ui.message("emr.yes") }', 5);
			}
			else {
				table.fnFilter('', 5);
			}
			table.fnDraw();
		});
	});
</script>