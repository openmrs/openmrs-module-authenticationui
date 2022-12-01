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

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ accountTitle }" }
    ];
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
                    initialValue: (account.gender ?: 'M'),
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
                <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/authenticationui/account/account.page?userId=${account.user.id}'" />
                <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
            </div>
        </form>

    <% } else { %>

        <style>
            .account-info-item {
                display: table-row;
            }
            .account-info-label {
                display: table-cell;
                font-weight: bold;
                padding-right: 20px;
                white-space: nowrap;
            }
            .account-info-value {
                display: table-cell;
                white-space: nowrap;
            }
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
        </style>

        <div class="account-section">
            <div id="content" class="container-fluid">
                <div class="dashboard clear row">
                    <div class="col-12 col-lg-8">
                        <div class="row">
                            <div class="col-12 col-lg-12">

                                <div class="info-section">
                                    <div class="info-header">
                                        <h3>${ ui.message("authenticationui.account.personDetails") }</h3>
                                    </div>
                                    <div class="account-info-item">
                                        <span class="account-info-label">${ ui.message("authenticationui.account.givenName") }: </span>
                                        <span class="account-info-value">${ account.givenName }</span>
                                    </div>
                                    <div class="account-info-item">
                                        <span class="account-info-label">${ ui.message("authenticationui.account.familyName") }: </span>
                                        <span class="account-info-value">${ account.familyName }</span>
                                    </div>
                                    <div class="account-info-item">
                                        <span class="account-info-label">${ ui.message("authenticationui.account.gender") }: </span>
                                        <span class="account-info-value">${ account.gender ? ui.message("authenticationui.account.gender." + account.gender) : "" }</span>
                                    </div>
                                </div>

                                <div class="info-section">
                                    <div class="info-header">
                                        <h3>${ ui.message("authenticationui.account.userDetails") }</h3>
                                    </div>

                                    <div class="account-info-item">
                                        <span class="account-info-label">${ ui.message("authenticationui.account.username") }: </span>
                                        <span class="account-info-value">${ account.user.username }</span>
                                    </div>

                                    <div class="account-info-item">
                                        <span class="account-info-label">${ ui.message("authenticationui.account.email") }: </span>
                                        <span class="account-info-value">${ account.email ?: '' }</span>
                                    </div>

                                    <% if (account.phoneNumberAttributeType) { %>
                                        <div class="account-info-item">
                                            <span class="account-info-label">${ ui.message("authenticationui.account.phoneNumber") }: </span>
                                            <span class="account-info-value">${ account.phoneNumber ?: '' }</span>
                                        </div>
                                    <% } %>
                                    <div class="account-info-item">
                                        <span class="account-info-label">${ ui.message("authenticationui.account.defaultLocale") }: </span>
                                        <span class="account-info-value">${ currentLocaleDisplay }</span>
                                    </div>
                                    <% if (account.defaultLocationUserProperty) { %>
                                    <div class="account-info-item">
                                        <span class="account-info-label">${ ui.message("authenticationui.account.defaultLocation") }: </span>
                                        <span class="account-info-value">${ account.defaultLocationName ?: '' }</span>
                                    </div>
                                    <% } %>
                                </div>

                            <% if (twoFactorAvailable) { %>

                                <div class="info-section">
                                    <div class="info-header">
                                        <h3>${ ui.message("authenticationui.account.2fa") }</h3>
                                    </div>

                                    <div class="account-info-item">
                                        <span class="account-info-label">${ ui.message("authenticationui.2fa.status") }: </span>
                                        <span class="account-info-value">${ ui.message(account.twoFactorAuthenticationMethod ? "authenticationui.2fa.enabled" : "authenticationui.2fa.disabled") }</span>
                                    </div>

                                    <% if (account.twoFactorAuthenticationMethod) { %>
                                    <div class="account-info-item">
                                        <span class="account-info-label">${ ui.message("authenticationui.2fa.method") }: </span>
                                        <span class="account-info-value">${ ui.message("authenticationui." + account.twoFactorAuthenticationMethod + ".name") }</span>
                                    </div>
                                    <% } %>
                                </div>

                            <% } %>

                        </div>
                    </div>
                </div>
                <div class="dashboard col-12 col-lg-4 p-0">
                    <div class="action-section">
                        <ul class="float-left">
                            <h3 >${ ui.message("authenticationui.actions") }</h3>
                            <li class="float-left">
                                <a class="float-left" href="${ui.pageLink("authenticationui", "account/account", [ edit: true, userId: user.id ])}">
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
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
<% } %>

</div>