package tasks;

import tasks.enm.TaskType;

public class Task extends AbstractTask {
private String title;
    public Task(String name, String description) {

        super(name, description);
        this.taskType = TaskType.TASK;
    }


    @Override
    public String toString() {
        return "tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", type=" + type +
                '}';
    }
}


