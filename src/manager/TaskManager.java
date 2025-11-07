package manager;

import tasks.AbstractTask;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    // МЕТОДЫ ДЛЯ TASK
    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    void createTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int id);

    //МЕТОДЫ ДЛЯ EPIC
    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int epicIdForDelete);

    //Методы ДЛЯ SUBTASKS
    List<SubTask> getAllSubtasks();

    void deleteAllSubtasks();

    SubTask getSubtaskById(int id);

    void createSubtask(SubTask subtask);

    void updateSubtask(SubTask subtask);

    void deleteSubtaskById(int id);

    List<AbstractTask> getHistory();

    List<SubTask> getSubtasksByEpicId(int epicId);

    List<AbstractTask> getPrioritizedTasks();


}