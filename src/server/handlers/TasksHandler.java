package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.BaseHttpHandler;
import server.QueryParser;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = QueryParser.parse(query);

            switch (method) {
                case "GET":
                    handleGet(exchange, params);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, params);
                    break;
                default:
                    sendServerError(exchange, "Unsupported method: " + method);
            }
        } catch (RuntimeException e) {
            handleManagerException(exchange, e);
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, Map<String, String> params) throws IOException {
        if (params.containsKey("id")) {
            int id = Integer.parseInt(params.get("id"));
            Task task = manager.getTaskById(id);
            if (task == null) {
                sendNotFound(exchange);
            } else {
                sendOk(exchange, gson.toJson(task));
            }
        } else {
            List<Task> all = manager.getAllTasks();
            sendOk(exchange, gson.toJson(all));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
        if (body.isEmpty()) {
            sendServerError(exchange, "Task body is empty");
            return;
        }

        Task task;
        try {
            task = gson.fromJson(body, Task.class);
        } catch (Exception e) {
            sendServerError(exchange, "Invalid JSON for Task: " + e.getMessage());
            return;
        }

        if (task == null) {
            sendServerError(exchange, "Task body is empty after parsing");
            return;
        }

        if (task.getId() == 0) {
            try {
                manager.createTask(task);
                sendCreated(exchange, gson.toJson(task));
            } catch (RuntimeException e) {
                handleManagerException(exchange, e);
            }
        } else {
            try {
                manager.updateTask(task);
                sendOk(exchange, gson.toJson(task));
            } catch (RuntimeException e) {
                handleManagerException(exchange, e);
            }
        }
    }

    private void handleDelete(HttpExchange exchange, Map<String, String> params) throws IOException {
        if (params.containsKey("id")) {
            int id = Integer.parseInt(params.get("id"));
            try {
                manager.deleteTaskById(id);
                sendOk(exchange, "{\"status\":\"deleted\"}");
            } catch (RuntimeException e) {
                handleManagerException(exchange, e);
            }
        } else {
            manager.deleteAllTasks();
            sendOk(exchange, "{\"status\":\"all deleted\"}");
        }
    }
}
