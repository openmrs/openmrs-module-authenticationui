<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authenticationui.verifyEmail.title") ])
%>

<h2>${ ui.message("authenticationui.verifyEmail.title") }</h2>

<div class="account-section">
    <% if (success) { %>
        <div class="note success">
            <span class="icon"><i class="icon-ok small"></i></span>
            <span class="text">${ ui.message("authenticationui.verifyEmail.success") }</span>
        </div>
    <% } else { %>
        <div class="note error">
            <span class="icon"><i class="icon-warning-sign small"></i></span>
            <span class="text">${ ui.message(errorCode) }</span>
        </div>
    <% } %>

    <p>
        <% if (context.authenticated) { %>
            <a href="${ ui.pageLink("authenticationui", "account/userAccount") }">${ ui.message("authenticationui.verifyEmail.returnToAccount") }</a>
        <% } else { %>
            <a href="${ ui.pageLink("authenticationui", "login/login") }">${ ui.message("authenticationui.verifyEmail.returnToLogin") }</a>
        <% } %>
    </p>
</div>
