<%
    def accountTitle = ownAccount ? ui.message("authenticationui.myAccount.title") : ui.format(account.user.person)
    ui.decorateWith("appui", "standardEmrPage", [ title: accountTitle ])
    ui.includeCss("authenticationui", "account.css", -60)

    def genderOptions = [ [label: ui.message("authenticationui.account.gender.M"), value: 'M'],
                          [label: ui.message("authenticationui.account.gender.F"), value: 'F'] ]

    def allowedLocalesOptions = []
    allowedLocales.each {
        def displayLanguage = it.getDisplayLanguage(account.defaultLocale)
        displayLanguage = (displayLanguage == "Haitian" ? "Haitian Creole" : displayLanguage)
        allowedLocalesOptions.push([ label: displayLanguage, value: it ]);
    }

    def allowedDefaultLocations = []
    locations.each {
        allowedDefaultLocations.push([ label: it.name, value: it.id.toString() ])
    }

    def currentLocaleDisplay = account.defaultLocale.getDisplayLanguage(account.defaultLocale)
    currentLocaleDisplay = (currentLocaleDisplay == "Haitian" ? "Haitian Creole" : currentLocaleDisplay)

    def editUserUrl = ui.pageLink("authenticationui", "account/userAccount", [ edit: true, userId: user.id ])
    def adminUserUrl = editUserUrl;
    if (sysAdmin) {
        adminUserUrl = authenticationUiConfig.getAdminEditUserPageUrl(ui, user.id)
    }
%>

<style>
    .task {
        display: block;
        text-align: center;
    }
    .task img {
        margin: auto;
        vertical-align: middle;
    }
</style>

<%= ui.includeFragment("authenticationui", "accountBreadcrumbs", [ userId: user.id ]) %>

<script type="text/javascript">

    function unlockAccount() {
        jq.post(emr.fragmentActionLink("authenticationui", "accountAction", "unlock", { userId: ${user.id} }), function (data) {
            emr.successMessage(data.message);
            jq('#locked-warning').hide();
        }, 'json').error(function(xhr) {
            emr.handleError(xhr);
        });
    }

    function disableAccount() {
        jq.post(emr.fragmentActionLink("authenticationui", "accountAction", "disable", { userId: ${user.id} }), function (data) {
            emr.successMessage(data.message);
            jq('#disabled-warning').show();
            jq('#disable-account-action').hide();
            jq('#enable-account-action').show();
        }, 'json').error(function(xhr) {
            emr.handleError(xhr);
        });
    }

    function enableAccount() {
        jq.post(emr.fragmentActionLink("authenticationui", "accountAction", "enable", { userId: ${user.id} }), function (data) {
            emr.successMessage(data.message);
            jq('#disabled-warning').hide();
            jq('#disable-account-action').show();
            jq('#enable-account-action').hide();
        }, 'json').error(function(xhr) {
            emr.handleError(xhr);
        });
    }
</script>

<h2>${ accountTitle }</h2>

<div class="account-section">
    <% if (editMode) { %>

        <form method="post" id="accountForm" autocomplete="off">

            ${ ui.includeFragment("uicommons", "field/text", [
                    label: ui.message("authenticationui.account.givenName"),
                    formFieldName: "givenName",
                    initialValue: (account.givenName ?: '')
            ])}

            ${ ui.includeFragment("uicommons", "field/text", [
                    label: ui.message("authenticationui.account.familyName"),
                    formFieldName: "familyName",
                    initialValue: (account.familyName ?: '')
            ])}

            ${ ui.includeFragment("uicommons", "field/radioButtons", [
                    label: ui.message("authenticationui.account.gender"),
                    formFieldName: "gender",
                    initialValue: (account.gender ?: ''),
                    options: genderOptions
            ])}

            ${ ui.includeFragment("uicommons", "field/text", [
                    label: ui.message("authenticationui.account.email"),
                    formFieldName: "email",
                    initialValue: (account.email ?: '')
            ])}

            <% if (account.phoneNumberAttributeType) { %>
                ${ ui.includeFragment("uicommons", "field/text", [
                        label: ui.message("authenticationui.account.phoneNumber"),
                        formFieldName: "phoneNumber",
                        initialValue: (account.phoneNumber ?: '')
                ])}
            <% } %>

            <p>
                ${ ui.includeFragment("uicommons", "field/dropDown", [
                        label: ui.message("authenticationui.account.defaultLocale"),
                        emptyOptionLabel: ui.message("authenticationui.action.chooseOne"),
                        formFieldName: "defaultLocale",
                        initialValue: (account.defaultLocale ?: ''),
                        options: allowedLocalesOptions
                ])}
            </p>

            <% if (account.defaultLocationUserProperty) { %>
                <p>
                    ${ ui.includeFragment("uicommons", "field/dropDown", [
                            label: ui.message("authenticationui.account.defaultLocation"),
                            emptyOptionLabel: ui.message("authenticationui.action.chooseOne"),
                            formFieldName: "defaultLocationId",
                            initialValue: (account.defaultLocationId ?: ''),
                            options: allowedDefaultLocations
                    ])}
                </p>
            <% } %>

            <div>
                <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/authenticationui/account/userAccount.page?userId=${account.user.id}'" />
                <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
            </div>
        </form>

    <% } else { %>

        <style>
            .float-left {
                float: left;
                clear: left;
                width: 97.91666%;
            }
            .warning {
                width: 100%;
                background-color: yellow;
                padding: 10px;
            }
            .dashboard .action-section {
                margin-top:0;
                padding: 10px;
                border: none;
            }
            .info-header {
                padding-top: 10px;
            }
        </style>

        <div id="locked-warning" class="note warning" ${!account.locked ? "style=\"display:none\"" : ""}>
            <span class="icon"><i class="icon-warning-sign medium"></i></span>
            <span class="text">
                <strong>${ ui.message("authenticationui.account.locked.title") }</strong>
                <em>${ ui.message("authenticationui.account.locked.description") }</em>
            </span>
        </div>

        <div id="disabled-warning" class="note warning" ${account.enabled ? "style=\"display:none\"" : ""}>
            <span class="icon"><i class="icon-warning-sign medium"></i></span>
            <span class="text">
                <strong>${ ui.message("authenticationui.account.disabled.title") }</strong>
                <em>${ ui.message("authenticationui.account.disabled.description") }</em>
            </span>
        </div>

        <div class="account-section">
            <div id="content" class="container-fluid">
                <div class="dashboard row">
                    <div class="col-12 col-lg-8">

                        <div class="row info-header">
                            <h3>${ ui.message("authenticationui.account.personDetails") }</h3>
                        </div>
                        <div class="row">
                            <span class="col-4 account-info-label">${ ui.message("authenticationui.account.givenName") }: </span>
                            <span class="col-8 account-info-value">${ account.givenName }</span>
                        </div>
                        <div class="row">
                            <span class="col-4 account-info-label">${ ui.message("authenticationui.account.familyName") }: </span>
                            <span class="col-8 account-info-value">${ account.familyName }</span>
                        </div>
                        <div class="row">
                            <span class="col-4 account-info-label">${ ui.message("authenticationui.account.gender") }: </span>
                            <span class="col-8 account-info-value">${ account.gender ? ui.message("authenticationui.account.gender." + account.gender) : "" }</span>
                        </div>

                        <div class="row info-header">
                            <h3>${ ui.message("authenticationui.account.userDetails") }</h3>
                        </div>

                        <div class="row">
                            <span class="col-4 account-info-label">${ ui.message("authenticationui.account.username") }: </span>
                            <span class="col-8 account-info-value">${ account.user.username }</span>
                        </div>

                        <div class="row">
                            <span class="col-4 account-info-label">${ ui.message("authenticationui.account.email") }: </span>
                            <span class="col-8 account-info-value">${ account.email ?: '' }</span>
                        </div>

                        <% if (account.phoneNumberAttributeType) { %>
                        <div class="row">
                            <span class="col-4 account-info-label">${ ui.message("authenticationui.account.phoneNumber") }: </span>
                            <span class="col-8 account-info-value">${ account.phoneNumber ?: '' }</span>
                        </div>
                        <% } %>
                        <div class="row">
                            <span class="col-4 account-info-label">${ ui.message("authenticationui.account.defaultLocale") }: </span>
                            <span class="col-8 account-info-value">${ currentLocaleDisplay }</span>
                        </div>
                        <% if (account.defaultLocationUserProperty) { %>
                        <div class="row">
                            <span class="col-4 account-info-label">${ ui.message("authenticationui.account.defaultLocation") }: </span>
                            <span class="col-8 account-info-value">${ account.defaultLocationName ?: '' }</span>
                        </div>
                        <% } %>

                        <% if (twoFactorAvailable) { %>

                        <div class="row info-header">
                            <h3>${ ui.message("authenticationui.account.2fa") }</h3>
                        </div>

                        <div class="row">
                            <span class="col-4 account-info-label">${ ui.message("authenticationui.2fa.status") }: </span>
                            <span class="col-8 account-info-value">${ ui.message(account.twoFactorAuthenticationMethod ? "authenticationui.2fa.enabled" : "authenticationui.2fa.disabled") }</span>
                        </div>

                        <% if (account.twoFactorAuthenticationMethod) { %>
                        <div class="row">
                            <span class="col-4 account-info-label">${ ui.message("authenticationui.2fa.method") }: </span>
                            <span class="col-8 account-info-value">${ ui.message("authenticationui." + account.twoFactorAuthenticationMethod + ".name") }</span>
                        </div>
                        <% } %>

                        <% } %>
                    </div>
                    <div class="col-12 col-lg-4 p-0">
                        <div class="action-section">
                            <ul class="float-left">
                                <h3 >${ ui.message("authenticationui.actions") }</h3>
                                <li class="float-left">
                                    <a class="float-left" href="${ editUserUrl }">
                                        <div class="row">
                                            <div class="col-1 col-lg-2">
                                                <i class="fas fa-fw fa-user"></i>
                                            </div>
                                            <div class="col-11 col-lg-10">
                                                ${ ui.message("authenticationui.action.editAccount") }
                                            </div>
                                        </div>
                                    </a>
                                </li>
                                <li class="float-left">
                                    <a class="float-left" href="${ ui.pageLink("authenticationui", "account/changePassword", [userId: user.id]) }">
                                        <div class="row">
                                            <div class="col-1 col-lg-2">
                                                <i class="fas fa-fw fa-lock"></i>
                                            </div>
                                            <div class="col-11 col-lg-10">
                                                ${ ui.message("authenticationui.action.changePassword") }
                                            </div>
                                        </div>
                                    </a>
                                </li>
                                <li class="float-left">
                                    <a class="float-left" href="${ ui.pageLink("authenticationui", "account/changeSecurityQuestion", [userId: user.id]) }">
                                        <div class="row">
                                            <div class="col-1 col-lg-2">
                                                <i class="fas fa-fw fa-question"></i>
                                            </div>
                                            <div class="col-11 col-lg-10">
                                                ${ ui.message("authenticationui.action.changeSecretQuestion") }
                                            </div>
                                        </div>
                                    </a>
                                </li>
                                <% if (twoFactorAvailable) { %>
                                    <li class="float-left">
                                        <a class="float-left" href="${ ui.pageLink("authenticationui", "account/twoFactorSetup", [userId: user.id]) }">
                                            <div class="row">
                                                <div class="col-1 col-lg-2">
                                                    <i class="fas fa-fw fa-user-lock"></i>
                                                </div>
                                                <div class="col-11 col-lg-10">
                                                    ${ ui.message("authenticationui.action.change2fa") }
                                                </div>
                                            </div>
                                        </a>
                                    </li>
                                <% } %>
                                <% if (sysAdmin) { %>
                                    <% if (!adminUserUrl.equals(editUserUrl)) { %>
                                        <li class="float-left">
                                            <a class="float-left" href="${ adminUserUrl }">
                                                <div class="row">
                                                    <div class="col-1 col-lg-2">
                                                        <i class="fas fa-fw fa-user"></i>
                                                    </div>
                                                    <div class="col-11 col-lg-10">
                                                        ${ ui.message("authenticationui.action.administerAccount") }
                                                    </div>
                                                </div>
                                            </a>
                                        </li>
                                    <% } %>
                                    <%  if (account.locked) { %>
                                        <li class="float-left">
                                            <a class="float-left" href="javascript:unlockAccount();">
                                                <div class="row">
                                                    <div class="col-1 col-lg-2">
                                                        <i class="fas fa-fw fa-unlock"></i>
                                                    </div>
                                                    <div class="col-11 col-lg-10">
                                                        ${ ui.message("authenticationui.action.unlockAccount") }
                                                    </div>
                                                </div>
                                            </a>
                                        </li>
                                    <% } %>
                                    <li id="disable-account-action" class="float-left" ${!account.enabled || ownAccount ? "style=\"display:none\"" : ""}>
                                        <a class="float-left" href="javascript:disableAccount();">
                                            <div class="row">
                                                <div class="col-1 col-lg-2">
                                                    <i class="fas fa-fw fa-unlock"></i>
                                                </div>
                                                <div class="col-11 col-lg-10">
                                                    ${ ui.message("authenticationui.action.disableAccount") }
                                                </div>
                                            </div>
                                        </a>
                                    </li>
                                    <li id="enable-account-action" class="float-left" ${account.enabled || ownAccount ? "style=\"display:none\"" : ""}>
                                        <a class="float-left" href="javascript:enableAccount();">
                                            <div class="row">
                                                <div class="col-1 col-lg-2">
                                                    <i class="fas fa-fw fa-unlock"></i>
                                                </div>
                                                <div class="col-11 col-lg-10">
                                                    ${ ui.message("authenticationui.action.enableAccount") }
                                                </div>
                                            </div>
                                        </a>
                                    </li>
                                <% } %>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    <% } %>
</div>