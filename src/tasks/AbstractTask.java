package tasks;

import tasks.enm.Status;

import java.util.Objects;


public abstract class AbstractTask {

    protected String name;
    protected String description;
    protected int id;
    protected Status type;

    protected AbstractTask(String name, String description) {
        this.name = name;
        this.description = description;
        this.type = Status.NEW;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        AbstractTask abstractTask = (AbstractTask) object;
        return id == abstractTask.id;
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
}
