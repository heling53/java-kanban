package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.BaseHttpHandler;
import server.QueryParser;
import tasks.SubTask;
import tasks.enm.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = QueryParser.parse(query);

            if (httpMethod == HttpMethod.GET && path.endsWith("/epic")) {
                if (params.containsKey("id")) {
                    int epicId = Integer.parseInt(params.get("id"));
                    try {
                        List<SubTask> list = manager.getSubtasksByEpicId(epicId);
                        sendOk(exchange, gson.toJson(list));
                    } catch (RuntimeException e) {
                        handleManagerException(exchange, e);
                    }
                } else {
                    sendServerError(exchange, "epic id missing");
                }
                return;
            }

            switch (httpMethod) {
                case GET:
                    if (params.containsKey("id")) {
                        int id = Integer.parseInt(params.get("id"));
                        SubTask sub = manager.getSubtaskById(id);
                        if (sub == null) sendNotFound(exchange);
                        else sendOk(exchange, gson.toJson(sub));
                    } else {
                        List<SubTask> all = manager.getAllSubtasks();
                        sendOk(exchange, gson.toJson(all));
                    }
                    break;
                case POST:
                    InputStream is = exchange.getRequestBody();
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                    if (body.isEmpty()) {
                        sendServerError(exchange, "Subtask body empty");
                        return;
                    }
                    SubTask subtask = gson.fromJson(body, SubTask.class);
                    if (subtask.getId() == 0) {
                        try {
                            manager.createSubtask(subtask);
                            sendCreated(exchange, gson.toJson(subtask));
                        } catch (RuntimeException e) {
                            handleManagerException(exchange, e);
                        }
                    } else {
                        try {
                            manager.updateSubtask(subtask);
                            sendOk(exchange, gson.toJson(subtask));
                        } catch (RuntimeException e) {
                            handleManagerException(exchange, e);
                        }
                    }
                    break;
                case DELETE:
                    if (params.containsKey("id")) {
                        int id = Integer.parseInt(params.get("id"));
                        try {
                            manager.deleteSubtaskById(id);
                            sendOk(exchange, "{\"status\":\"deleted\"}");
                        } catch (RuntimeException e) {
                            handleManagerException(exchange, e);
                        }
                    } else {
                        manager.deleteAllSubtasks();
                        sendOk(exchange, "{\"status\":\"all deleted\"}");
                    }
                    break;
                default:
                    sendServerError(exchange, "Unsupported method");
            }
        } catch (RuntimeException e) {
            handleManagerException(exchange, e);
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }
}
