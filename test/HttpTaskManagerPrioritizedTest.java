package test;

import org.junit.jupiter.api.Test;
import tasks.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerPrioritizedTest extends tests.HttpTaskServerTest {

    @Test
    void testGetEmptyPrioritized() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testGetPrioritizedTasks() throws Exception {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setStartTime(LocalDateTime.now().plusHours(1));
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Task 2", "Description 2");
        task2.setStartTime(LocalDateTime.now().plusHours(2));
        task2.setDuration(Duration.ofMinutes(30));

        manager.createTask(task1);
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<?> prioritized = gson.fromJson(response.body(), List.class);
        assertEquals(2, prioritized.size());
    }
}