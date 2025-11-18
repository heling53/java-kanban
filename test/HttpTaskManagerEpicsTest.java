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

class HttpTaskManagerEpicsTest extends tests.HttpTaskServerTest {

    @Test
    void testCreateEpic() throws Exception {
        Epic epic = new Epic("Test Epic", "Test Description");

        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals("Test Epic", epics.get(0).getName());
    }

    @Test
    void testGetEpicById() throws Exception {
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/epics?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic.getId(), responseEpic.getId());
        assertEquals("Test Epic", responseEpic.getName());
    }

    @Test
    void testGetAllEpics() throws Exception {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<?> epics = gson.fromJson(response.body(), List.class);
        assertEquals(2, epics.size());
    }

    @Test
    void testDeleteEpic() throws Exception {
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.createEpic(epic);
        int epicId = epic.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/epics?id=" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertTrue(manager.getAllEpics().isEmpty());
    }
}