package manager.impl;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.enm.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;

    protected final Map<Integer, Task> taskMap = new HashMap<>();
    protected final Map<Integer, Epic> epicsMap = new HashMap<>();
    protected final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();


    // МЕТОДЫ ДЛЯ TASK
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public void deleteAllTasks() {
        taskMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskMap.get(id);
        if (task != null) {
            addToHistory(task);
        }
        return task;
    }

    @Override
    public void createTask(Task task) {
        taskMap.put(nextId, task);
        task.setId(nextId);
        nextId++;
    }

    @Override
    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }


    }

    @Override
    public void deleteTaskById(int id) {
        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
            historyManager.remove(id);
        }

    }


    //МЕТОДЫ ДЛЯ EPIC
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    @Override
    public void deleteAllEpics() {
        epicsMap.clear();
        subTaskMap.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicsMap.get(id);
        if (epic != null) {
            addToHistory(epic);
        }
        return epic;
    }

    @Override
    public void createEpic(Epic epic) {
        epicsMap.put(nextId, epic);
        epic.setId(nextId);
        nextId++;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicsMap.containsKey(epic.getId())) {
            epicsMap.put(epic.getId(), epic);
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }
    }

    @Override
    public void deleteEpicById(int epicIdForDelete) {
        if (epicsMap.containsKey(epicIdForDelete)) {
            Epic epicForDelete = epicsMap.get(epicIdForDelete);
            epicsMap.remove(epicIdForDelete);
            historyManager.remove(epicIdForDelete);
            for (int subTaskIdForDelete : epicForDelete.getSubTasksId()) {
                subTaskMap.remove(subTaskIdForDelete);
                historyManager.remove(subTaskIdForDelete);
            }
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }
    }

    //Методы ДЛЯ SUBTASKS
    @Override
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTaskMap.values());
    }

    @Override
    public void deleteAllSubtasks() {
        for (SubTask subTask : getAllSubtasks()) {
            getEpicById(subTask.getEpicId()).clearAllSubTasksID();
            updateStatusEpic((epicsMap.get(subTask.getEpicId())));
        }
        subTaskMap.clear();
    }

    @Override
    public SubTask getSubtaskById(int id) {
        SubTask subTask = subTaskMap.get(id);
        if (subTask != null) {
            addToHistory(subTask);
        }
        return subTask;
    }

    @Override
    public void createSubtask(SubTask subtask) {
        subTaskMap.put(nextId, subtask);
        subtask.setId(nextId);
        nextId++;
        epicsMap.get(subtask.getEpicId()).addSubTasksID(subtask.getId());
        updateStatusEpic(epicsMap.get(subtask.getEpicId()));
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        if (subTaskMap.containsKey(subtask.getId())) {
            subTaskMap.put(subtask.getId(), subtask);
            int epicID = subtask.getEpicId();
            updateStatusEpic(epicsMap.get(epicID));
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }
    }

    @Override
    public void deleteSubtaskById(int id) {

        if (subTaskMap.containsKey(id)) {
            int epicId = subTaskMap.get(id).getEpicId();
            Epic epic = getEpicById(epicId);
            epic.removeSubTasksID(id);
            subTaskMap.remove(id);
            historyManager.remove(id);
            updateStatusEpic(epic);
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }
    }

    //ДОП МЕТОДЫ
    @Override
    public List<SubTask> getSubtasksByEpicId(int epicId) {
        List<Integer> subtaskIds = epicsMap.get(epicId).getSubTasksId();
        if (subtaskIds == null) {
            throw new RuntimeException("Нет такой задачи!");
        }
        List<SubTask> subtaskFind = new ArrayList<>();
        for (Integer subtaskId : subtaskIds) {
            subtaskFind.add(subTaskMap.get(subtaskId));
        }
        return subtaskFind;

    }

    protected void updateStatusEpic(Epic epic) {
        if (epic.getSubTasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean isDone = false;
        boolean isNew = false;
        List<Integer> subtaskIds = epic.getSubTasksId();
        for (Integer subtaskId : subtaskIds) {
            Status type = subTaskMap.get(subtaskId).getStatus();
            if (type == Status.DONE) {
                if (isNew) {
                    epic.setStatus(Status.IN_PROGRESS);

                    return;
                } else {
                    isDone = true;
                }
            } else if (type == Status.NEW) {
                if (isDone) {
                    epic.setStatus(Status.IN_PROGRESS);

                    return;
                } else {
                    isNew = true;
                }
            }
        }
        if (isDone) {

            epic.setStatus(Status.DONE);
        } else if (isNew) {
            epic.setStatus(Status.NEW);
        }
    }

    private void addToHistory(AbstractTask task) {
        historyManager.add(task);
    }

    @Override
    public List<AbstractTask> getHistory() {
        return historyManager.getHistory();
    }
}
