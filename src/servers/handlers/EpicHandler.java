package servers.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EpicHandler implements HttpHandler {
    private static final Gson gson = new Gson();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String response;

        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = 200;
                    response = gson.toJson(taskManager.getAllEpics());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Epic epic = taskManager.getEpicById(id);
                        if (epic != null) {
                            response = gson.toJson(epic);
                        } else {
                            response = "Эпик с данным id не найден";
                        }
                        statusCode = 200;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат id";
                    }
                }
                break;
            case "POST":
                String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Epic epic = gson.fromJson(bodyRequest, Epic.class);
                    if (epic.getSubtaskIds() == null) {
                        statusCode = 400;
                        response = "Для Эпика требуется передать параметр subtasks";
                    }
                    else {
                        int id = epic.getId();
                        if (taskManager.getEpicById(id) != null) {
                            taskManager.updateTask(epic);
                            statusCode = 200;
                            response = "Эпик с id=" + id + " обновлен";
                        } else {
                            Epic epicCreated = taskManager.createEpic(epic);
                            int idCreated = epicCreated.getId();
                            statusCode = 201;
                            response = "Создан эпик с id=" + idCreated;
                        }
                    }
                } catch (JsonSyntaxException e) {
                    response = "Неверный формат запроса";
                    statusCode = 400;
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.deleteAllEpics();
                    statusCode = 204;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteEpicById(id);
                        statusCode = 204;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат id";
                    }
                }
                break;
            default:
                statusCode = 400;
                response = "Некорректный запрос";
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}