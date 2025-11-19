package tests;

import com.google.gson.Gson;
import manager.TaskManager;
import manager.impl.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.handlers.HttpTaskServer;
import utils.GsonFactory;

import java.io.IOException;

public abstract class HttpTaskServerTest {
    protected TaskManager manager;
    protected HttpTaskServer server;
    protected Gson gson;
    protected final int PORT = 8080;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        gson = GsonFactory.createGson();
        server = new HttpTaskServer(manager, PORT);
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }
}