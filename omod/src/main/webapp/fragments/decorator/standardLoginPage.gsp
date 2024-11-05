<%
    // ***** Adapted from appui module
    def title = config.title ?: ui.message("authenticationui.login.title")
    def headerIconUrl = config.authenticationUiConfig.getHeaderLogoUrl(ui);
    def homePageUrl = config.authenticationUiConfig.getHomePageUrl(ui);

    ui.includeJavascript("uicommons", "jquery-1.12.4.min.js", Integer.MAX_VALUE)
    ui.includeJavascript("uicommons", "jquery-ui-1.9.2.custom.min.js", Integer.MAX_VALUE - 10)
    ui.includeJavascript("uicommons", "underscore-min.js", Integer.MAX_VALUE - 10)
    ui.includeJavascript("uicommons", "knockout-2.2.1.js", Integer.MAX_VALUE - 15)
    ui.includeJavascript("uicommons", "emr.js", Integer.MAX_VALUE - 15)

    ui.includeCss("uicommons", "styleguide/jquery-ui-1.9.2.custom.min.css", Integer.MAX_VALUE - 10)

    // toastmessage plugin: https://github.com/akquinet/jquery-toastmessage-plugin/wiki
    ui.includeJavascript("uicommons", "jquery.toastmessage.js", Integer.MAX_VALUE - 20)
    ui.includeCss("uicommons", "styleguide/jquery.toastmessage.css", Integer.MAX_VALUE - 20)

    // simplemodal plugin: http://www.ericmmartin.com/projects/simplemodal/
    ui.includeJavascript("uicommons", "jquery.simplemodal.1.4.4.min.js", Integer.MAX_VALUE - 20)
%>
<!DOCTYPE html>
<html>

    <head>
        <title>${ title }</title>
        <link rel="shortcut icon" type="image/ico" href="/${ ui.contextPath() }/images/openmrs-favicon.ico"/>
        <link rel="icon" type="image/png" href="/${ ui.contextPath() }/images/openmrs-favicon.png"/>
        ${ ui.resourceLinks() }
        <script src="/${ui.contextPath()}/csrfguard" type="text/javascript"></script>
    </head>

    <body>

        <script type="text/javascript">
            var OPENMRS_CONTEXT_PATH = '${ ui.contextPath() }';
        </script>

        <header>
            <div class="logo">
                <a href="${ homePageUrl }">
                    <img class="login-header-image" src="${ headerIconUrl }"/>
                </a>
            </div>
        </header>

        <div id="body-wrapper" class="container">

            ${ ui.includeFragment("uicommons", "infoAndErrorMessage") }

            <% if (authenticationSession.getErrorMessage()) { %>
                <div id="error-message" class="note-container">
                    <div class="note error">
                        ${ ui.message(authenticationSession.getErrorMessage()) }
                    </div>
                </div>
            <% } %>

            <div id="content" class="container-fluid">
                <%= config.content %>
            </div>

        </div>
    </body>

</html>
