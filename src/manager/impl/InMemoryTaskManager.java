package manager.impl;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.enm.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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
        for (Task task : taskMap.values()) {
            prioritizedTasks.remove(task);
        }
        taskMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskMap.get(id);
        if (task != null) {
            addToHistory(task);
            return task;
        }
        throw new RuntimeException("Задача с id=" + id + " не найдена");
    }

    @Override
    public void createTask(Task task) {
        checkTimeIntersection(task);
        taskMap.put(nextId, task);
        task.setId(nextId);
        nextId++;
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            Task oldTask = taskMap.get(task.getId());
            prioritizedTasks.remove(oldTask);
            checkTimeIntersection(task);
            taskMap.put(task.getId(), task);
            if (task.getStartTime() != null) prioritizedTasks.add(task);
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }


    }

    @Override
    public void deleteTaskById(int id) {
        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(taskMap.get(id));
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
            return epic;
        }
        throw new RuntimeException("Эпик с id=" + id + " не найден");
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
            updateEpicTime(epicsMap.get(subTask.getEpicId()));
        }
        subTaskMap.clear();
    }

    @Override
    public SubTask getSubtaskById(int id) {
        SubTask subTask = subTaskMap.get(id);
        if (subTask != null) {
            addToHistory(subTask);
            return subTask;
        }
        throw new RuntimeException("Подзадача с id=" + id + " не найдена");
    }

    @Override
    public void createSubtask(SubTask subtask) {
        subTaskMap.put(nextId, subtask);
        subtask.setId(nextId);
        nextId++;
        epicsMap.get(subtask.getEpicId()).addSubTasksID(subtask.getId());
        if (subtask.getStartTime() != null) prioritizedTasks.add(subtask);
        updateStatusEpic(epicsMap.get(subtask.getEpicId()));
        updateEpicTime(epicsMap.get(subtask.getEpicId()));
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        if (subTaskMap.containsKey(subtask.getId())) {
            subTaskMap.put(subtask.getId(), subtask);
            int epicID = subtask.getEpicId();
            if (subtask.getStartTime() != null) prioritizedTasks.add(subtask);
            updateStatusEpic(epicsMap.get(epicID));
            updateEpicTime(epicsMap.get(epicID));
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
            updateEpicTime(epic);
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
        boolean isInProgress = false;

        List<Integer> subtaskIds = epic.getSubTasksId();
        for (Integer subtaskId : subtaskIds) {
            Status type = subTaskMap.get(subtaskId).getStatus();

            if (type == Status.IN_PROGRESS) {
                // Если есть хоть одна IN_PROGRESS - сразу IN_PROGRESS
                epic.setStatus(Status.IN_PROGRESS);
                return;
            } else if (type == Status.DONE) {
                if (isNew) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
                isDone = true;
            } else if (type == Status.NEW) {
                if (isDone) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
                isNew = true;
            }
        }

        if (isDone && isNew) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (isDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    private void addToHistory(AbstractTask task) {
        historyManager.add(task);
    }

    protected void updateEpicTime(Epic epic) {
        List<SubTask> subTasks = getSubtasksByEpicId(epic.getId());

        if (subTasks.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            return;
        }

        LocalDateTime start = subTasks.stream()
                .filter(t -> t.getStartTime() != null)
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime end = subTasks.stream()
                .filter(t -> t.getEndTime() != null)
                .map(SubTask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration totalDuration = (start != null && end != null) ? Duration.between(start, end) : Duration.ZERO;

        epic.setStartTime(start);
        epic.setDuration(totalDuration);
    }

    @Override
    public List<AbstractTask> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected final Set<AbstractTask> prioritizedTasks = new TreeSet<>(
            (a, b) -> {
                if (a.getStartTime() == null && b.getStartTime() == null) return Integer.compare(a.getId(), b.getId());
                if (a.getStartTime() == null) return 1;
                if (b.getStartTime() == null) return -1;
                int compare = a.getStartTime().compareTo(b.getStartTime());
                if (compare == 0) return Integer.compare(a.getId(), b.getId());
                return compare;
            }
    );

    private boolean isOverlapping(AbstractTask a, AbstractTask b) {
        if (a.getStartTime() == null || a.getEndTime() == null ||
                b.getStartTime() == null || b.getEndTime() == null) {
            return false;
        }

        return !a.getEndTime().isBefore(b.getStartTime()) &&
                !a.getStartTime().isAfter(b.getEndTime());
    }

    protected boolean isOverlappingWithAny(AbstractTask task) {
        return prioritizedTasks.stream()
                .anyMatch(t -> !t.equals(task) && isOverlapping(t, task));
    }

    protected void checkTimeIntersection(AbstractTask task) {
        if (task.getStartTime() != null && isOverlappingWithAny(task)) {
            throw new RuntimeException("Задача пересекается по времени с другой!");
        }
    }


    @Override
    public List<AbstractTask> getHistory() {
        return historyManager.getHistory();
    }
}
