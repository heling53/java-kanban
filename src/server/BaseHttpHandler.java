package server;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text == null ? new byte[0] : text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendOk(HttpExchange exchange, String body) throws IOException {
        sendText(exchange, body, 200);
    }

    protected void sendCreated(HttpExchange exchange, String body) throws IOException {
        sendText(exchange, body, 201);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Not found\"}", 404);
    }

    protected void sendConflict(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"error\":\"" + escape(message) + "\"}", 406);
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"error\":\"" + escape(message) + "\"}", 500);
    }

    // Примитивное экранирование кавычек в сообщениях
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }

    protected void handleManagerException(HttpExchange exchange, RuntimeException e) throws IOException {
        String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();

        if (msg.contains("пересек") || msg.contains("пересека") || msg.contains("intersection")) {
            sendConflict(exchange, msg);
        } else if (msg.contains("нет такой") || msg.contains("не найден") ||
                msg.contains("not found") || msg.contains("не найдена")) {
            sendNotFound(exchange);
        } else {
            sendServerError(exchange, msg);
        }
    }
}
