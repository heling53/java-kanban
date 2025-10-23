package tasks;

import tasks.enm.TaskType;

public class SubTask extends AbstractTask {
    private final int epicId;

    public SubTask(String name, String description, int epicID) {
        super(name, description);
        this.epicId = epicID;
        this.taskType = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return "tasks.SubTask{" +
                "epicID=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", type=" + type +
                '}';
    }
}
