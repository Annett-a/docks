<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title><c:out value="${errorTitle != null ? errorTitle : 'Ошибка'}"/></title>
    <link rel="stylesheet" href="${ctx}/assets/css/app.css"/>
</head>
<body>
<main class="card">
    <h1><c:out value="${errorTitle}"/></h1>
    <p class="muted"><c:out value="${errorText}"/></p>
    <c:if test="${not empty missingId}">
        <p class="muted">Идентификатор: <code class="mono"><c:out value="${missingId}"/></code></p>
    </c:if>
    <p><a class="link" href="${ctx}/">На главную</a></p>
</main>
</body>
</html>
