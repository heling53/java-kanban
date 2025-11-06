package manager.impl;

import exceptions.ManagerSaveException;
import manager.TaskManager;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.enm.Status;
import tasks.enm.TaskType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File csvFile;


    public FileBackedTaskManager(File csvFile) {
        this.csvFile = csvFile;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(SubTask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }


    public void save() {
        try (FileWriter fw = new FileWriter(csvFile)) {
            fw.write("id,type,name,status,description,duration,startTime,endTime,epic\n");
            for (Task task : getAllTasks()) {
                fw.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                fw.write(toString(epic) + "\n");
            }
            for (SubTask subtask : getAllSubtasks()) {
                fw.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }

    }

    private AbstractTask createTaskFromString(String value) {
        String[] split = value.split(",", -1);
        int id = Integer.parseInt(split[0]);
        TaskType type = TaskType.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        String durationField = split[5];
        String startTimeField = split[6];
        String epic = split[8];

        AbstractTask task = switch (type) {
            case TASK -> new Task(name, description);
            case EPIC -> new Epic(name, description);
            case SUBTASK -> {
                int epicId = Integer.parseInt(epic);
                yield new SubTask(name, description, epicId);
            }
            default -> throw new ManagerSaveException("Неизвестный тип задачи: " + type);
        };
        task.setId(id);
        task.setStatus(status);

        if(!durationField.isEmpty()){
            task.setDuration(Duration.ofMinutes(Long.parseLong(durationField)));
        }
        if(!startTimeField.isEmpty()){
            task.setStartTime(LocalDateTime.parse(startTimeField));
        }
        return task;

    }

    public static FileBackedTaskManager loadTasksFromFile(File csvFile) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(csvFile);
        try {
            if (!csvFile.exists()) {
                return taskManager;
            }
            String content = Files.readString(csvFile.toPath());
            String[] lines = content.split("\n");
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (!line.isEmpty()) {
                    AbstractTask task = taskManager.createTaskFromString(line);

                    switch (task.getTaskType()) {
                        case TASK:
                            taskManager.taskMap.put(task.getId(), (Task) task);
                            break;
                        case EPIC:
                            taskManager.epicsMap.put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            taskManager.subTaskMap.put(task.getId(), (SubTask) task);
                            SubTask subtask = (SubTask) task;
                            Epic epic = taskManager.epicsMap.get(subtask.getEpicId());
                            if (epic != null) {
                                epic.addSubTasksID(subtask.getId());
                            }
                            break;
                    }
                    if (task.getId() > taskManager.nextId) {
                        taskManager.nextId = task.getId() + 1;
                    }
                    if (task.getStartTime() != null && task.getTaskType() != TaskType.EPIC) {
                        taskManager.prioritizedTasks.add(task);
                    }
                }
            }
            for (Epic epic : taskManager.epicsMap.values()) {
                taskManager.updateStatusEpic(epic);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
        return taskManager;

    }

    private String toString(AbstractTask task) {
        String epicIdField = "";

        if (task.getTaskType() == TaskType.SUBTASK) {
            SubTask subtask = (SubTask) task;
            epicIdField = String.valueOf(subtask.getEpicId());
        }
        String durationField = (task.getDuration() != null) ? String.valueOf(task.getDuration().toMinutes()) : "";
        String startTimeFiled = (task.getStartTime() != null) ? task.getStartTime().toString() : "";
        String endTimeFiled = (task.getEndTime() != null) ? task.getEndTime().toString() : "";

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getTaskType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                durationField,
                startTimeFiled,
                endTimeFiled,
                epicIdField
        );
    }

    public static void main(String[] args) {

        File dataFile = new File("task_data.csv");

        if (dataFile.exists()) {
            dataFile.delete();
        }

        FileBackedTaskManager manager1 = new FileBackedTaskManager(dataFile);

        Task task1 = new Task("Задача 1", "Test1");
        task1.setStatus(Status.IN_PROGRESS);
        manager1.createTask(task1);

        Task task2 = new Task("Задача 2", "Test2");
        manager1.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "TestEpic1");
        manager1.createEpic(epic1);

        SubTask subtask1 = new SubTask("Подзадача 1", "TestSubtask1", epic1.getId());
        subtask1.setStatus(Status.DONE);
        manager1.createSubtask(subtask1);

        SubTask subtask2 = new SubTask("Подзадача 2", "TestSubtask2", epic1.getId());
        manager1.createSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "TestEpic2");
        manager1.createEpic(epic2);

        SubTask subtask3 = new SubTask("Подзадача 3", "TestSubtask3", epic2.getId());
        manager1.createSubtask(subtask3);

        printAllTasks(manager1);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadTasksFromFile(dataFile);

        printAllTasks(manager2);


        manager2.getTaskById(task1.getId());
        manager2.getEpicById(epic1.getId());
        manager2.getSubtaskById(subtask1.getId());

        List<AbstractTask> history;
        history = manager2.getHistory();
        for (int i = 0; i < history.size(); i++) {
            AbstractTask task = history.get(i);
            System.out.println("  " + (i + 1) + ". " + task.getTaskType() + " \"" + task.getName() + "\" (ID: " + task.getId() + ")");
        }


        try {
            String content = Files.readString(dataFile.toPath());
            System.out.println(content);
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

    }

    private static void printAllTasks(FileBackedTaskManager manager) {
        System.out.println("Задачи (" + manager.getAllTasks().size() + "):");
        for (Task task : manager.getAllTasks()) {
            System.out.println("  [TASK] ID: " + task.getId() + ", \"" + task.getName() + "\", Status: " + task.getStatus());
        }

        System.out.println("Эпики (" + manager.getAllEpics().size() + "):");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println("  [EPIC] ID: " + epic.getId() + ", \"" + epic.getName() + "\", Status: " + epic.getStatus() + ", Подзадач: " + manager.getSubtasksByEpicId(epic.getId()).size());
        }

        System.out.println("Подзадачи (" + manager.getAllSubtasks().size() + "):");
        for (SubTask subtask : manager.getAllSubtasks()) {
            System.out.println("  [SUBTASK] ID: " + subtask.getId() + ", \"" + subtask.getName() + "\", Status: " + subtask.getStatus() + ", Epic ID: " + subtask.getEpicId());
        }
    }


}
