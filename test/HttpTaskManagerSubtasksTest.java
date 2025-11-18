package test;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.enm.Status;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerSubtasksTest extends tests.HttpTaskServerTest {

    @Test
    void testCreateSubtask() throws Exception {
        Epic epic = new Epic("Parent Epic", "Epic Description");
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Test Subtask", "Test Description", epic.getId());
        subtask.setStatus(Status.NEW);

        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<SubTask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals("Test Subtask", subtasks.get(0).getName());
    }

    @Test
    void testGetSubtasksByEpic() throws Exception {
        Epic epic = new Epic("Parent Epic", "Epic Description");
        manager.createEpic(epic);

        SubTask subtask1 = new SubTask("Subtask 1", "Description 1", epic.getId());
        SubTask subtask2 = new SubTask("Subtask 2", "Description 2", epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/subtasks/epic?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<?> subtasks = gson.fromJson(response.body(), List.class);
        assertEquals(2, subtasks.size());
    }

    @Test
    void testDeleteSubtask() throws Exception {
        Epic epic = new Epic("Parent Epic", "Epic Description");
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Test Subtask", "Test Description", epic.getId());
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/subtasks?id=" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getSubtaskById(subtask.getId()));
    }
}