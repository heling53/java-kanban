import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasksID = new ArrayList<>();

    public Epic(String Name, String Description) {
        super(Name, Description);

    }

    public List<Integer> getSubTasksID() {
        return subTasksID;
    }
    public void addSubTasksID(Integer subTasksID) {
        this.subTasksID.add(subTasksID);
    }
    public void removeSubTasksID(Integer subTasksID) {
        this.subTasksID.remove(subTasksID);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasksID=" + subTasksID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", type=" + type +
                '}';
    }
}
