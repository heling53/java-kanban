package tasks;

public class Task extends AbstractTask {
private String title;
    public Task(String name, String description) {
        super(name, description);
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


