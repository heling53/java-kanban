import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HistoryManager history = Managers.getDefaultHistory();

        Task t1 = new Task("Task 1", "Desc 1");
        manager.createTask(t1);
        Task t2 = new Task("Task 2", "Desc 2");
        manager.createTask(t2);

        Epic e1 = new Epic("Epic 1", "Big epic");
        manager.createEpic(e1);
        SubTask s1 = new SubTask("Sub 1", "Inside epic 1", e1.getId());
        manager.createSubtask(s1);
        SubTask s2 = new SubTask("Sub 2", "Inside epic 1", e1.getId());
        manager.createSubtask(s2);
        SubTask s3 = new SubTask("Sub 3", "Inside epic 1", e1.getId());
        manager.createSubtask(s3);
        System.out.println("История:");
        manager.getTaskById(t1.getId());
        manager.getTaskById(t2.getId());
        manager.getSubtaskById(s3.getId());
        manager.getHistory().forEach(System.out::println);
        System.out.println();
        manager.getEpicById(e1.getId());
        manager.getSubtaskById(s1.getId());
        manager.getSubtaskById(s3.getId());
        manager.getHistory().forEach(System.out::println);
        System.out.println();
        manager.getTaskById(t1.getId());
        manager.getSubtaskById(s2.getId());// повтор
        manager.getHistory().forEach(System.out::println);
        System.out.println();
        manager.deleteTaskById(t1.getId());
        manager.getHistory().forEach(System.out::println);
        System.out.println();
        manager.getSubtaskById(s1.getId());
        manager.getSubtaskById(s2.getId());
        manager.getSubtaskById(s3.getId());
        manager.deleteEpicById(e1.getId());
        manager.getHistory().forEach(System.out::println);
    }
}
