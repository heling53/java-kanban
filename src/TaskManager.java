import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int nextId = 1;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private HashMap<Integer, SubTask> subtasks = new HashMap<>();



    // МЕТОДЫ ДЛЯ TASK
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void createTask(Task task) {
        tasks.put(nextId, task);
        task.setId(nextId);
        nextId++;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }


    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
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
                subtasks.remove(subTaskIdForDelete);
            }
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }
    }

    //Методы ДЛЯ SUBTASKS
    public List<SubTask> getAllSubtasks() {

        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {

        subtasks.clear();
    }

    public SubTask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void createSubtask(SubTask subtask) {
        subtasks.put(nextId, subtask);
        subtask.setId(nextId);
        nextId++;
        epicsMap.get(subtask.getEpicID()).addSubTasksID(subtask.getId());
    }

    public void updateSubtask(SubTask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            int epicID = subtask.getEpicID();
            updateStatusEpic(epicsMap.get(epicID));
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }
    }

    public void deleteSubtaskById(int id) {

        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicID();
            Epic epic = getEpicById(epicId);
            epic.removeSubTasksID(id);
            subtasks.remove(id);
            updateStatusEpic(epic);
        } else {
            throw new RuntimeException("Нет такой задачи!");
        }
    }

    //ДОП МЕТОДЫ
    public List<SubTask> getSubtasksByEpicId(int epicId) {
        Epic abc = epicsMap.get(epicId);
        if (abc == null) {
            throw new RuntimeException("<UNK> <UNK> <UNK>!");
        }
        List<Integer> subtaskIds = abc.getSubTasksID();
        List<SubTask> subtasks2 = new ArrayList<>();
        for (Integer subtaskId : subtaskIds) {
            subtasks2.add(subtasks.get(subtaskId));
        }
        return subtasks2;

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
            Status type = subtasks.get(subtaskId).getType();
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
