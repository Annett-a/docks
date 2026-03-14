<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Банковский документ</title>
    <link rel="stylesheet" href="${ctx}/assets/css/app.css"/>
</head>
<body>
<main class="card">
    <h1>Банковский документ</h1>
    <p class="muted">Только просмотр. Редактирование/удаление не предусмотрены.</p>

    <dl class="kv">
        <dt>ID</dt>
        <dd><code class="mono"><c:out value="${doc.id}"/></code></dd>

        <dt>Тип документа</dt>
        <dd><span class="badge"><c:out value="${doc.docType}"/></span></dd>

        <dt>Создан</dt>
        <dd><fmt:formatDate value="${createdAt}" pattern="dd.MM.yyyy HH:mm:ss"/></dd>

        <dt>Пользователь</dt>
        <dd><c:out value="${doc.userFio}"/> (<code class="mono"><c:out value="${doc.userId}"/></code>)</dd>

        <dt>Номер карты</dt>
        <dd><c:out value="${doc.cardNumber}"/></dd>
    </dl>

    <p><a class="link" href="${ctx}/">На главную</a></p>
</main>
</body>
</html>
