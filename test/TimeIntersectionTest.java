package test;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeIntersectionTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldDetectOverlappingTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        task1.setDuration(Duration.ofHours(2));

        Task task2 = new Task("Task 2", "Description 2");
        task2.setStartTime(LocalDateTime.of(2024, 1, 1, 11, 0));
        task2.setDuration(Duration.ofHours(2));

        taskManager.createTask(task1);

        assertThrows(RuntimeException.class, () -> {
            taskManager.createTask(task2);
        });
    }

    @Test
    void shouldAllowNonOverlappingTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        task1.setDuration(Duration.ofHours(1));

        Task task2 = new Task("Task 2", "Description 2");
        task2.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        task2.setDuration(Duration.ofHours(1));

        taskManager.createTask(task1);
        assertDoesNotThrow(() -> taskManager.createTask(task2));
    }

    @Test
    void shouldAllowTasksWithoutStartTime() {
        Task task1 = new Task("Task 1", "Description 1");

        Task task2 = new Task("Task 2", "Description 2");
        task2.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        task2.setDuration(Duration.ofHours(1));

        assertDoesNotThrow(() -> taskManager.createTask(task1));
        assertDoesNotThrow(() -> taskManager.createTask(task2));
    }
}