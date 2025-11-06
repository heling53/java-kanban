package test;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.enm.Status;

import static org.junit.jupiter.api.Assertions.*;

class EpicStatusBoundaryTest {
    private TaskManager taskManager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        epic = new Epic("Test Epic", "Test Description");
        taskManager.createEpic(epic);
    }

    @Test
    void shouldHaveStatusNewWhenAllSubtasksNew() {
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", epic.getId());

        taskManager.createSubtask(subTask1);
        taskManager.createSubtask(subTask2);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.NEW, updatedEpic.getStatus());
    }

    @Test
    void shouldHaveStatusDoneWhenAllSubtasksDone() {
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", epic.getId());

        taskManager.createSubtask(subTask1);
        taskManager.createSubtask(subTask2);

        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subTask1);
        taskManager.updateSubtask(subTask2);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus());
    }

    @Test
    void shouldHaveStatusInProgressWhenSubtasksNewAndDone() {
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", epic.getId());

        taskManager.createSubtask(subTask1);
        taskManager.createSubtask(subTask2);

        subTask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subTask1);
        // subTask2 остается NEW

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void shouldHaveStatusInProgressWhenAllSubtasksInProgress() {
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", epic.getId());

        taskManager.createSubtask(subTask1);
        taskManager.createSubtask(subTask2);

        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subTask1);
        taskManager.updateSubtask(subTask2);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void shouldHaveStatusNewWhenNoSubtasks() {
        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.NEW, updatedEpic.getStatus());
    }
}