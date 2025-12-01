package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import utils.GsonFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpTaskServer {
    private final TaskManager manager;
    private final Gson gson;
    private HttpServer server;
    private final int port;

    public HttpTaskServer(TaskManager manager, int port) {
        this.manager = manager;
        this.port = port;
        this.gson = GsonFactory.createGson();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/tasks", new TasksHandler(manager, gson));
        server.createContext("/epics", new EpicsHandler(manager, gson));
        server.createContext("/subtasks", new SubtasksHandler(manager, gson));
        server.createContext("/history", new HistoryHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(manager, gson));
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("Server started at http://localhost:" + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server stopped");
        }
    }

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(manager, 8080);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
