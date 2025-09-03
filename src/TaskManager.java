import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int nextId = 1;

    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();


    // МЕТОДЫ ДЛЯ TASK
    public List<AbstractTask> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public void deleteAllTasks() {
        taskMap.clear();
    }

    public AbstractTask getTaskById(int id) {
        return taskMap.get(id);
    }

    public void createTask(Task task) {
        taskMap.put(nextId, task);
        task.setId(nextId);
        nextId++;
    }

    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }


    }

    public void deleteTaskById(int id) {
        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }

    }


    //МЕТОДЫ ДЛЯ EPIC
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    public void deleteAllEpics() {
        epicsMap.clear();
        subTaskMap.clear();
    }

    public Epic getEpicById(int id) {
        return epicsMap.get(id);
    }

    public void createEpic(Epic epic) {
        epicsMap.put(nextId, epic);
        epic.setId(nextId);
        nextId++;
    }

    public void updateEpic(Epic epic) {
        if (epicsMap.containsKey(epic.getId())) {
            epicsMap.put(epic.getId(), epic);
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }
    }

    public void deleteEpicById(int epicIdForDelete) {
        if (epicsMap.containsKey(epicIdForDelete)) {
            Epic epicForDelete = epicsMap.get(epicIdForDelete);
            epicsMap.remove(epicIdForDelete);
            for (int subTaskIdForDelete : epicForDelete.getSubTasksID()) {
                subTaskMap.remove(subTaskIdForDelete);
            }
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }
    }

    //Методы ДЛЯ SUBTASKS
    public List<SubTask> getAllSubtasks() {
        if (subTaskMap.isEmpty()) {
            throw new RuntimeException("Задач нет!");
        }
        return new ArrayList<>(subTaskMap.values());
    }

    public void deleteAllSubtasks() {

        subTaskMap.clear();
    }

    public SubTask getSubtaskById(int id) {
        return subTaskMap.get(id);
    }

    public void createSubtask(SubTask subtask) {
        subTaskMap.put(nextId, subtask);
        subtask.setId(nextId);
        nextId++;
        epicsMap.get(subtask.getEpicID()).addSubTasksID(subtask.getId());
    }

    public void updateSubtask(SubTask subtask) {
        if (subTaskMap.containsKey(subtask.getId())) {
            subTaskMap.put(subtask.getId(), subtask);
            int epicID = subtask.getEpicID();
            updateStatusEpic(epicsMap.get(epicID));
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }
    }

    public void deleteSubtaskById(int id) {

        if (subTaskMap.containsKey(id)) {
            int epicId = subTaskMap.get(id).getEpicID();
            Epic epic = getEpicById(epicId);
            epic.removeSubTasksID(id);
            subTaskMap.remove(id);
            updateStatusEpic(epic);
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }
    }

    //ДОП МЕТОДЫ
    public List<SubTask> getSubtasksByEpicId(int epicId) {
        List<Integer> subtaskIds = epicsMap.get(epicId).getSubTasksID();
        if (subtaskIds == null) {
            throw new RuntimeException("Нет такой задачи!");
        }
        List<SubTask> subtaskFind = new ArrayList<>();
        for (Integer subtaskId : subtaskIds) {
            subtaskFind.add(subTaskMap.get(subtaskId));
        }
        return subtaskFind;

    }

    private void updateStatusEpic(Epic epic) {
        if (epic.getSubTasksID().isEmpty()) {
            epic.setType(Status.NEW);
            return;
        }
        boolean isDone = false;
        boolean isNew = false;
        List<Integer> subtaskIds = epic.getSubTasksID();
        for (Integer subtaskId : subtaskIds) {
            Status type = subTaskMap.get(subtaskId).getType();
            if (type == Status.DONE) {
                if (isNew) {
                    epic.setType(Status.IN_PROGRESS);

                    return;
                } else {
                    isDone = true;
                }
            } else if (type == Status.NEW) {
                if (isDone) {
                    epic.setType(Status.IN_PROGRESS);

                    return;
                } else {
                    isNew = true;
                }
            }
        }
        if (isDone) {

            epic.setType(Status.DONE);
        } else if (isNew) {
            epic.setType(Status.NEW);
        }
    }

}
