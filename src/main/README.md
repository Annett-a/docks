Главная страница:

http://localhost:8086/

Если главная не открывается напрямую, можно открыть:
http://localhost:8086/index.jsp

Страница создания документа:
http://localhost:8086/create.html

Страница просмотра ссылок на документ:
http://localhost:8086/view.html

MVC (HTML)

Просмотр конкретного документа:
http://localhost:8086/documents/<id>

Пример ошибки 400 (битый UUID):
http://localhost:8086/documents/abc

Пример ошибки 404 (документ не найден):
http://localhost:8086/documents/00000000-0000-0000-0000-000000000000
REST API (JSON)
Получить документ по ID
GET http://localhost:8086/api/documents/<id>

Ошибка 400 (битый UUID):
GET http://localhost:8086/api/documents/abc

Ошибка 404 (документ не найден):
GET http://localhost:8086/api/documents/00000000-0000-0000-0000-000000000000

Создать документ вручную
POST http://localhost:8086/api/documents
Content-Type: application/json
