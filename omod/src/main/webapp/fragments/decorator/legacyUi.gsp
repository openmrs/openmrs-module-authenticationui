<%
    config.authenticationUiContext.requireAuthentication()
    def title = config.title ?: ui.message("openmrs.title")
    def homePageUrl = config.authenticationUiContext.config.getHomePageUrl(ui)

    ui.includeCss("uicommons", "datatables/dataTables_jui.css")

    ui.includeJavascript("uicommons", "jquery-1.12.4.min.js", Integer.MAX_VALUE)
    ui.includeJavascript("uicommons", "jquery-ui-1.9.2.custom.min.js", Integer.MAX_VALUE - 10)
    ui.includeJavascript("uicommons", "underscore-min.js", Integer.MAX_VALUE - 10)
    ui.includeJavascript("uicommons", "knockout-2.2.1.js", Integer.MAX_VALUE - 15)
    ui.includeJavascript("uicommons", "emr.js", Integer.MAX_VALUE - 15)

%>
<!DOCTYPE html>
<html>
<head>
    <title>${ title }</title>
    <link rel="shortcut icon" type="image/ico" href="/${ ui.contextPath() }/images/openmrs-favicon.ico"/>
    <link rel="icon" type="image/png" href="/${ ui.contextPath() }/images/openmrs-favicon.png"/>
    <script src="/${ui.contextPath()}/csrfguard" type="text/javascript"></script>
    <link rel="stylesheet" href="/${ui.contextPath()}/moduleResources/legacyui/css/openmrs.css" type="text/css" />
    <link rel="stylesheet" href="/${ui.contextPath()}/moduleResources/legacyui/css/openmrs_green.css" type="text/css" />
    <link rel="stylesheet" href="/${ui.contextPath()}/moduleResources/legacyui/css/style.css" type="text/css" />
    <link rel="stylesheet" href="/${ui.contextPath()}/scripts/jquery-ui/css/green/jquery-ui.custom.css" type="text/css" />
    ${ ui.resourceLinks() }
</head>

<body>
<div id="pageBody">
    <div id="userBar">
        <span id="userLoggedInAs" class="firstChild">
            ${ui.message("header.logged.in")} ${config.authenticationUiContext.authenticatedUser.personName}
        </span>
        <span id="userLogout">
            <a href='/${ui.contextPath()}/ms/logout'>${ui.message("header.logout")}</a>
        </span>
        <span>
            <a href="/${ui.contextPath()}/options.form">${ui.message("Navigation.options")}</a>
        </span>
        <span id="userHelp">
            <a href='/${ui.contextPath()}/help.htm'>${ui.message("header.help")}</a>
        </span>
    </div>

    <div id="banner">
        <a href="${homePageUrl}">
            <div id="logosmall"><img src="/${ui.contextPath()}/images/openmrs_logo_text_small.png" alt="OpenMRS Logo" style="border:0"/></div>
        </a>
        <table id="bannerbar" style="text-align:left;">
            <tr>
                <td id="logocell"><img src="/${ui.contextPath()}/images/openmrs_logo_white.gif" alt="" class="logo-reduced61" /></td>
                <td id="barcell">
                    <div class="barsmall" id="barsmall">
                        <% if (config.authenticationUiContext.hasPrivilege("View Navigation Menu")) { %>

                            <ul class="navList" style="margin:-4px;">
                                <li id="homeNavLink" class="firstChild">
                                    <a href="${homePageUrl}">${ui.message("Navigation.home")}</a>
                                </li>
                                <% if (config.authenticationUiContext.hasPrivilege("View Patients")) { %>
                                    <li id="findPatientNavLink">
                                        <a href="/${ui.contextPath()}/findPatient.htm">
                                            ${ui.message(config.authenticationUiContext.hasPrivilege("Add Patients") ? "Navigation.findCreatePatient" : "Navigation.findPatient")}
                                        </a>
                                    </li>
                                <% } %>
                                <% if (config.authenticationUiContext.hasPrivilege("View Concepts")) { %>
                                    <li id="dictionaryNavLink">
                                        <a href="/${ui.contextPath()}/dictionary/index.htm">${ui.message("Navigation.dictionary")}</a>
                                    </li>
                                <% } %>
                                <% if (config.authenticationUiContext.hasPrivilege("View Administration Functions")) { %>
                                    <li id="administrationNavLink">
                                        <a href="/${ui.contextPath()}/admin/index.htm">${ui.message("Navigation.administration")}</a>
                                    </li>
                                <% } %>
                            </ul>
                        <% } %>
                    </div>
                </td>
            </tr>
        </table>
    </div>

    <div id="content" class="container-fluid">
        <%= config.content %>
    </div>
</div>

<div id="footer">
    <div id="footerInner">
        <span id="localeOptions">
            <% org.openmrs.util.LocaleUtility.getLocalesInOrder().eachWithIndex {locale, index -> %>
                ${ index == 0 ? "" : "|" }
                <% if (config.authenticationUiContext.locale == locale) { %>
                    ${locale.getDisplayName(config.authenticationUiContext.locale)}
                <% } else { %>
                    <a href="&lang=${locale}">
                        ${locale.getDisplayName(config.authenticationUiContext.locale)}
                    </a>
                <% } %>
            <% } %>
        </span>

        <span id="buildDate">${ui.message("footer.lastBuild")}: ${ org.openmrs.web.WebConstants.BUILD_TIMESTAMP}</span>
        <span id="codeVersion">${ui.message("footer.version")}: ${ org.openmrs.util.OpenmrsConstants.OPENMRS_VERSION }</span>

        <span id="poweredBy">
            <a href="http://openmrs.org">
                ${ui.message("footer.poweredBy")}
                <img style="border: 0; vertical-align: top;" src="/${ui.contextPath()}/images/openmrs_logo_tiny.png"/>
            </a>
        </span>
    </div>
</div>

</body>
</html>