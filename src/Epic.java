import java.util.ArrayList;
import java.util.List;

public class Epic extends AbstractTask {
    private final List<Integer> subTasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);

    }

    public List<Integer> getSubTasksId() {
        return subTasksId;
    }

    public void addSubTasksID(Integer subTasksID) {
        this.subTasksId.add(subTasksID);
    }

    public void removeSubTasksID(Integer subTasksID) {
        this.subTasksId.remove(subTasksID);
    }

    public void clearAllSubTasksID() {
        this.subTasksId.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasksID=" + subTasksId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", type=" + type +
                '}';
    }
}
