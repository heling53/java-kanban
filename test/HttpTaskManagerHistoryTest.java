package test;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerHistoryTest extends tests.HttpTaskServerTest {

    @Test
    void testGetEmptyHistory() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testGetHistory() throws Exception {
        Task task = new Task("Test Task", "Test Description");
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.createTask(task);
        manager.createEpic(epic);

        // Вызываем задачи для добавления в историю
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<?> history = gson.fromJson(response.body(), List.class);
        assertEquals(2, history.size());
    }
}