package test;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import manager.impl.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import tasks.AbstractTask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    @Test
    void add() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        historyManager.add(task);
        final List<AbstractTask> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

}