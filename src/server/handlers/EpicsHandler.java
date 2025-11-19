package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.BaseHttpHandler;
import server.QueryParser;
import tasks.Epic;
import tasks.enm.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = QueryParser.parse(query);

            switch (httpMethod) {
                case GET:
                    if (params.containsKey("id")) {
                        int id = Integer.parseInt(params.get("id"));
                        Epic epic = manager.getEpicById(id);
                        if (epic == null) sendNotFound(exchange);
                        else sendOk(exchange, gson.toJson(epic));
                    } else {
                        List<Epic> all = manager.getAllEpics();
                        sendOk(exchange, gson.toJson(all));
                    }
                    break;
                case POST:
                    InputStream is = exchange.getRequestBody();
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                    if (body.isEmpty()) {
                        sendServerError(exchange, "Epic body is empty");
                        return;
                    }
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (epic.getId() == 0) {
                        manager.createEpic(epic);
                        sendCreated(exchange, gson.toJson(epic));
                    } else {
                        try {
                            manager.updateEpic(epic);
                            sendOk(exchange, gson.toJson(epic));
                        } catch (RuntimeException e) {
                            handleManagerException(exchange, e);
                        }
                    }
                    break;
                case DELETE:
                    if (params.containsKey("id")) {
                        int id = Integer.parseInt(params.get("id"));
                        try {
                            manager.deleteEpicById(id);
                            sendOk(exchange, "{\"status\":\"deleted\"}");
                        } catch (RuntimeException e) {
                            handleManagerException(exchange, e);
                        }
                    } else {
                        manager.deleteAllEpics();
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
