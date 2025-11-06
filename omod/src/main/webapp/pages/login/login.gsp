<%
    ui.decorateWith("authenticationui", "standardLoginPage", [
            title: ui.message("authenticationui.login.title"),
            authenticationUiConfig: authenticationUiConfig
    ])
%>
<style>
    #login-location-section {
        padding-top: 10px;
    }
    .location-list-item {
        border-top: 1px solid #EFEFEF;
        border-bottom: 0 !important;
        vertical-align: top;
    }
    #login-form ul.select {
        width: unset;
    }
    #login-form input[type=text], #login-form input[type=password] {
        min-width: 100%;
    }
    #login-page fieldset {
        display: block;
    }
</style>


<% if (authenticationUiConfig.getLoginWarningIfNotChrome()) { %>
    <script type="text/javascript">
        jq(document).ready(function() {
            var ua = window.navigator.userAgent;
            if (!/chrom(e|ium)/.test(ua.toLowerCase())) {
                jq('#browser-warning-message').show();
            }
        });
    </script>
    <div id="browser-warning-message" class="note-container" style="display:none">
        <div class="note error">
            <div class="text">
                <i class="icon-remove medium"></i>
                <p>${ ui.message(authenticationUiConfig.getLoginWarningIfNotChrome()) }</p>
            </div>
            <div class="close-icon"><i class="icon-remove"></i></div>
        </div>
    </div>
<% } %>

<div id="login-page">

    <form id="login-form" method="post" autocomplete="off">

        <h1>${ ui.message(authenticationUiConfig.getLoginWelcomeMessage()) }</h1>

        <fieldset>

            <legend>
                <i class="icon-lock small"></i>
                ${ ui.message("authenticationui.login.loginHeading") }
            </legend>

            <div class="container-fluid">

                <div class="row justify-content-center">
                    <div class="col">
                        <label for="username">
                            ${ ui.message("authenticationui.login.username") }:
                        </label>
                        <input id="username" type="text" name="username" placeholder="${ ui.message("authenticationui.login.username.placeholder") }"/>
                    </div>

                    <div class="col">
                        <label for="password">
                            ${ ui.message("authenticationui.login.password") }:
                        </label>
                        <input id="password" type="password" name="password" placeholder="${ ui.message("authenticationui.login.password.placeholder") }"/>
                    </div>
                </div>

                <!-- only show location selector if there are multiple locations to choose from -->
                <% if (locations.size > 1) { %>
                    <div id="login-location-section" class="row justify-content-center">
                        <div class="col">
                            <label for="sessionLocation">
                                ${ ui.message("authenticationui.login.sessionLocation") }:
                            </label>
                            <ul id="sessionLocation" class="select">
                                <% locations.sort { ui.format(it) }.each { %>
                                    <li class="location-list-item" value="${it.id}">${ui.format(it)}</li>
                                <% } %>
                            </ul>
                        </div>
                    </div>
                <% } %>

                <div id="login-location-section" class="row">
                    <div class="col-auto mr-auto"></div>
                    <div class="col-auto">
                        <input id="login-button" class="confirm" type="submit" value="${ ui.message("authenticationui.login.button") }"/>
                    </div>
                </div>
                <div id="login-location-section" class="row">
                    <div class="col-auto mr-auto"></div>
                    <div class="col-auto">
                        <a id="cant-login" href="javascript:void(0)">
                            <i class="icon-question-sign small"></i>
                            ${ ui.message("authenticationui.login.cannotLogin") }
                        </a>
                    </div>
                </div>

            </div>

            <input type="hidden" id="sessionLocationInput" name="sessionLocation"
                <% if (locations.size == 1) { %>
                    value="${locations[0].id}"
                <% } %>
                <% if (lastSessionLocation != null) { %>
                    value="${lastSessionLocation.id}"
                <% } %>
            />

        </fieldset>

    </form>

</div>

<div id="cannot-login-popup" class="dialog" style="display: none">
    <div class="dialog-header">
        <i class="icon-info-sign"></i>
        <h3>${ ui.message("authenticationui.login.cannotLogin") }</h3>
    </div>
    <div class="dialog-content">
        <% if (authenticationUiConfig.isAllowPasswordReset()) { %>
            <p class="dialog-instructions">${ ui.message("authenticationui.login.usernameOrEmail") }</p>
            <p id="password-reset-message" style="padding-bottom:10px; color:red;"></p>
            <p style="padding-bottom: 20px;">
                <input type="text" id="password-reset-username" size="35" autocomplete="off" data-lpignore="true"/>
            </p>
            <button class="cancel">${ ui.message("authenticationui.login.cancel") }</button>
            <button class="confirm">${ ui.message("authenticationui.login.requestPasswordReset") }</button>
        <% } else { %>
            <p class="dialog-instructions">${ ui.message("authenticationui.login.cannotLoginInstructions") }</p>
            <button class="cancel">${ ui.message("authenticationui.login.cancel") }</button>
        </div>
        <% } %>
    </div>
</div>

<script type="text/javascript">
    jq( document ).ready(function() {
        jq('#username').focus();

        updateSelectedOption = function() {
            jq('#sessionLocation li').removeClass('selected');
            var sessionLocationVal = jq('#sessionLocationInput').val();

            if (jq('#sessionLocation li').size() === 0) {
                jq('#login-button').removeClass('disabled');
                jq('#login-button').removeAttr('disabled');
            }
            else if(parseInt(sessionLocationVal, 10) > 0) {
                jq('#sessionLocation li[value|=' + sessionLocationVal + ']').addClass('selected');
                jq('#login-button').removeClass('disabled');
                jq('#login-button').removeAttr('disabled');
            }
            else if (${ !authenticationUiConfig.requireLoginLocation }) {
                jq('#login-button').removeClass('disabled');
                jq('#login-button').removeAttr('disabled');
            }
            else {
                jq('#login-button').addClass('disabled');
                jq('#login-button').attr('disabled','disabled');
            }
        };

        isUsernameValid = function(username) {
            return (username && username.length !== 0 && username.indexOf(' ') < 0);
        }

        updateSelectedOption();

        jq('#sessionLocation li').click( function() {
            jq('#sessionLocationInput').val(jq(this).attr("value"));
            updateSelectedOption();
        });

        var cannotLoginController = emr.setupConfirmationDialog({
            selector: '#cannot-login-popup',
            actions: {
                confirm: function() {
                    const username = jq("#password-reset-username").val();
                    if (!isUsernameValid(username)) {
                        jq("#password-reset-message").html('${ ui.escapeJs(ui.encodeHtmlContent(ui.message("authenticationui.login.error.invalidUsername"))) }');
                    }
                    else {
                        jq("#password-reset-message").html('');
                        jq.post(emr.fragmentActionLink("authenticationui", "resetPassword", "reset", { "username": username }));
                        emr.successMessage('${ ui.escapeJs(ui.encodeHtmlContent(ui.message("authenticationui.login.requestPasswordResponse"))) }');
                        cannotLoginController.close();
                        jq("#password-reset-username").val("");
                    }
                }
            }
        });
        jq('a#cant-login').click(function() {
            cannotLoginController.show();
        })
    });
</script>