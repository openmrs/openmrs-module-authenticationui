<%
    ui.decorateWith("appui", "standardEmrPage", [title: ui.message("authenticationui.activeUsers.title")])
	ui.includeCss("authenticationui", "account.css", -60)
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/${ui.contextPath()}/index.htm' },
        { label: "${ ui.message("authenticationui.systemAdministration.title")}", link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}' },
        { label: "${ ui.message("authenticationui.activeUsers.title")}" }
    ];
</script>

<h3>${  ui.message("authenticationui.activeUsers.title") }</h3>
<table id="active-users-table" style="width: 100%;">
	<thead>
		<tr>
			<th>${ ui.message("authenticationui.login.username")}</th>
			<th>${ ui.message("authenticationui.activeUsers.ipAddress") }</th>
			<th>${ ui.message("authenticationui.activeUsers.loginDate") }</th>
			<th>${ ui.message("authenticationui.activeUsers.lastActivityDate") }</th>
		</tr>
	</thead>
	<tbody>
		<% activeUsers.each {  %>
	 	<tr>
	 		<td>${ ui.format(it.username) }</td>
			<td>${ ui.format(it.ipAddress) }</td>
			<td>${ ui.format(it.loginDate) }</td>
			<td>${ ui.format(it.lastActivityDate) }</td>
		</tr>
		<% } %>
	</tbody>
</table>

<% if ( (activeUsers != null) && (activeUsers.size() > 0) ) { %>
	${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#active-users-table",
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
