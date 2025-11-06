package tasks;

import tasks.enm.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends AbstractTask {
    private final List<Integer> subTasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.taskType = TaskType.EPIC;
        this.duration = Duration.ZERO;

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

    public void updateTimeData(List<SubTask> subTasks) {
        if (subTasks.isEmpty() || subTasks == null) {
            this.startTime = null;
            this.endTime = null;
            this.duration = Duration.ZERO;
            return;
        }

        this.startTime = subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(t -> t != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        this.endTime = subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(t -> t != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        this.duration = subTasks.stream()
                .map(SubTask::getDuration)
                .filter(d -> d != null)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "subTasksID=" + subTasksId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", type=" + type +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                '}';
    }
}
