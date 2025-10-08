package test;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.AbstractTask;
import tasks.Task;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager = Managers.getDefaultHistory();
    TaskManager taskManager = Managers.getDefault();
    private Task task1, task2, task3;




    @BeforeEach
    void createTask() {
        task1 = new Task("Task 1", "Desc 1");
        taskManager.createTask(task1);
        task2 = new Task("Task 2", "Desc 2");
        taskManager.createTask(task2);
        task3 = new Task("Task 3", "Desc 3");
        taskManager.createTask(task3);
    }

    @Test
    void shouldAddAndRetrieveTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<AbstractTask> tasks = historyManager.getHistory();
        assertEquals(3, tasks.size());
        assertEquals(task1, tasks.get(0));
        assertEquals(task3, tasks.get(2));
    }

    @Test
    void shouldNotDuplicateTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // повтор

        List<AbstractTask> tasks = historyManager.getHistory();
        assertEquals(2, tasks.size());
        assertEquals(task2, tasks.get(0));
        assertEquals(task1, tasks.get(1));
    }

    @Test
    void shouldRemoveTaskById() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);
        List<AbstractTask> tasks = historyManager.getHistory();

        assertEquals(2, tasks.size());
        assertFalse(tasks.contains(task2));
    }

    @Test
    void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        historyManager.add(task);
        final List<AbstractTask> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }
}