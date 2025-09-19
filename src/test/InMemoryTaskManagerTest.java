package test;

import manager.Managers;
import manager.TaskManager;
import manager.impl.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.enm.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();

        task = new Task("Test Task", "Test Description");
        taskManager.createTask(task);

        epic = new Epic("Test Epic", "Test Epic Description");
        taskManager.createEpic(epic);

        subTask = new SubTask("Test SubTask", "Test SubTask Description", epic.getId());
        taskManager.createSubtask(subTask);
    }

    @Test
    void createTask() {
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createSubTaskAndEpic() {
        final SubTask savedSubTask = taskManager.getSubtaskById(subTask.getId());
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(epic, "Эпик не найдена.");
        assertNotNull(subTask, "Подзадача не найдена.");

        assertEquals(epic, savedEpic, "Задачи не совпадают.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");

    }

    @Test
    void deleteTask() {
        taskManager.deleteTaskById(task.getId());
        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNull(savedTask, "Задача не удалена.");
    }

    @Test
    void deleteSubTaskAndEpic() {
        taskManager.deleteSubtaskById(subTask.getId());
        final SubTask savedSubTask = taskManager.getSubtaskById(subTask.getId());
        assertNull(savedSubTask, "Задача не удалена.");

        taskManager.deleteEpicById(epic.getId());
        final Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNull(savedEpic, "Задача не удалена.");
    }

    @Test
    void UpdateTask() {
        Task updatedTask = new Task("Updated Task", "Updated Description");
        updatedTask.setId(task.getId());
        updatedTask.setType(Status.DONE);
        taskManager.updateTask(updatedTask);

        Task savedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated Task", savedTask.getName());
        assertEquals("Updated Description", savedTask.getDescription());
        assertEquals(Status.DONE, savedTask.getType());
    }

    @Test
    void GetAllEpics() {
        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
    }

    @Test
    void DeleteAllEpics() {
        taskManager.deleteAllEpics();
        List<Epic> epics = taskManager.getAllEpics();
        List<SubTask> subTasks = taskManager.getAllSubtasks();

        assertTrue(epics.isEmpty());
        assertTrue(subTasks.isEmpty());
    }

    @Test
    void UpdateEpic() {
        Epic updatedEpic = new Epic("Updated Epic", "Updated Description");
        updatedEpic.setId(epic.getId());
        List<Integer> idSubTasks = epic.getSubTasksId();
        for (int id : idSubTasks) {
            updatedEpic.addSubTasksID(id);
        }
        taskManager.updateEpic(updatedEpic);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals("Updated Epic", savedEpic.getName());
        assertEquals("Updated Description", savedEpic.getDescription());
    }

    @Test
    void DeleteAllSubtasks() {
        taskManager.deleteAllSubtasks();
        List<SubTask> subTasks = taskManager.getAllSubtasks();
        List<Integer> subTasksId = epic.getSubTasksId();
        assertEquals(0, subTasks.size());
        assertEquals(0, subTasksId.size());

    }

    @Test
    void UpdateSubtask() {
        SubTask updatedSubTask = new SubTask("Updated SubTask", "Updated Description", epic.getId());
        updatedSubTask.setType(Status.DONE);
        updatedSubTask.setId(subTask.getId());
        taskManager.updateSubtask(updatedSubTask);

        SubTask savedSubTask = taskManager.getSubtaskById(subTask.getId());
        assertEquals("Updated SubTask", savedSubTask.getName());
        assertEquals("Updated Description", savedSubTask.getDescription());
        assertEquals(Status.DONE, savedSubTask.getType());
        assertEquals(Status.DONE, epic.getType());
    }

    @Test
    void GetSubtasksByEpicId() {
        List<SubTask> subTasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertNotNull(subTasks);
        assertEquals(1, subTasks.size());
        assertEquals(subTask, subTasks.get(0));
    }

    @Test
    void testUpdateStatusEpicMixed() {
        SubTask secondSubTask = new SubTask("Second SubTask", "Description", epic.getId());
        taskManager.createSubtask(secondSubTask);

        SubTask doneSubTask = new SubTask("Done SubTask", "Description", epic.getId());
        doneSubTask.setType(Status.DONE);
        doneSubTask.setId(subTask.getId());
        taskManager.updateSubtask(doneSubTask);
        assertEquals(Status.IN_PROGRESS, epic.getType());

        secondSubTask.setType(Status.DONE);
        taskManager.updateSubtask(secondSubTask);
        assertEquals(Status.DONE, epic.getType());

    }

    @Test
    void testHistoryManagement() {
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subTask.getId());

        List<AbstractTask> history = taskManager.getHistory();
        assertEquals(3, history.size());
        assertTrue(history.contains(task));
        assertTrue(history.contains(epic));
        assertTrue(history.contains(subTask));
    }
}