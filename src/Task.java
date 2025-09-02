import java.util.Objects;

public class Task {
    protected String name;
    String description;
    int id;
    protected Status type;

    public Task(String Name, String Description) {
        this.name = Name;
        this.description = Description;
        this.type = Status.NEW;
    }

    public void newTask(String taskName, String taskDescription) {
    }

    @Override
    public boolean equals(Object object) {
        if (object == null ||
                getClass() != object.getClass())
            return false;
        Task task = (Task) object;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getType() {
        return type;
    }

    public void setType(Status type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", type=" + type +
                '}';
    }
}


