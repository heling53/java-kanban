package test;

import manager.impl.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldSaveAndLoadEmptyTasks() {
        File file = tempDir.resolve("empty_test.csv").toFile();
        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadTasksFromFile(file);

        assertTrue(manager2.getAllTasks().isEmpty());
        assertTrue(manager2.getAllEpics().isEmpty());
        assertTrue(manager2.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasksWithEpicsAndSubtasks() {
        File file = tempDir.resolve("full_test.csv").toFile();
        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);

        Task task = new Task("Test Task", "Test Description");
        manager1.createTask(task);

        Epic epic = new Epic("Test Epic", "Test Description");
        manager1.createEpic(epic);

        SubTask subTask = new SubTask("Test SubTask", "Test Description", epic.getId());
        manager1.createSubtask(subTask);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadTasksFromFile(file);

        assertEquals(1, manager2.getAllTasks().size());
        assertEquals(1, manager2.getAllEpics().size());
        assertEquals(1, manager2.getAllSubtasks().size());
    }

    @Test
    void shouldReturnEmptyManagerWhenLoadingFromNonExistentFile() {
        File nonExistentFile = new File("non_existent_directory/tasks.csv");

        FileBackedTaskManager manager = FileBackedTaskManager.loadTasksFromFile(nonExistentFile);

        assertNotNull(manager, "Менеджер должен быть создан");
        assertTrue(manager.getAllTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(manager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void shouldNotThrowExceptionWhenSavingToFile() {
        File file = tempDir.resolve("save_test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task = new Task("Test Task", "Test Description");

        assertDoesNotThrow(() -> manager.createTask(task));
    }

    @Test
    void shouldPreserveTaskDataAfterSaveAndLoad() {
        File file = tempDir.resolve("preserve_test.csv").toFile();
        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);

        Task originalTask = new Task("Original Task", "Original Description");
        originalTask.setStatus(tasks.enm.Status.IN_PROGRESS);
        manager1.createTask(originalTask);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadTasksFromFile(file);
        Task loadedTask = manager2.getTaskById(originalTask.getId());

        assertNotNull(loadedTask, "Задача должна быть загружена");
        assertEquals(originalTask.getName(), loadedTask.getName());
        assertEquals(originalTask.getDescription(), loadedTask.getDescription());
        assertEquals(originalTask.getStatus(), loadedTask.getStatus());
    }

    @Test
    void shouldPreserveEpicSubtasksRelationshipAfterSaveAndLoad() {
        File file = tempDir.resolve("epic_subtasks_test.csv").toFile();
        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);

        Epic epic = new Epic("Test Epic", "Test Description");
        manager1.createEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", epic.getId());
        manager1.createSubtask(subTask1);
        manager1.createSubtask(subTask2);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadTasksFromFile(file);
        Epic loadedEpic = manager2.getEpicById(epic.getId());

        assertNotNull(loadedEpic, "Эпик должен быть загружен");
        List<SubTask> loadedSubtasks = manager2.getSubtasksByEpicId(loadedEpic.getId());
        assertEquals(2, loadedSubtasks.size(), "Должны быть загружены 2 подзадачи");
    }
}