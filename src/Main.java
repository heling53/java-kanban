public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Task task1 = new Task("Ударить валеру","По лицу");
        Task task2 = new Task("Ударить Васю","По ноге");
        Epic epic = new Epic("Магазин","Сходить");
        Epic epic2 = new Epic("Школа","Купить");
        manager.createEpic(epic);
        manager.createEpic(epic2);
        SubTask subTask1 = new SubTask("Молоко","1л", epic.getId());
        SubTask subTask2 = new SubTask("Яблоки","2кг", epic.getId());
        SubTask subTask3 = new SubTask("Яблоки","3кг", epic.getId());
        SubTask subTask4 = new SubTask("Линейка","1шт", epic2.getId());
        SubTask subTask5 = new SubTask("Ручка","5шт", epic2.getId());

        manager.createTask(task1);
        manager.createTask(task2);

        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createSubtask(subTask4);
        manager.createSubtask(subTask5);

        subTask3.setId(subTask2.getId());

        subTask3.setType(Status.DONE);
        manager.updateSubtask(subTask3);
        System.out.println(
        );
        System.out.println();
        manager.getSubtasksByEpicId(epic.getId());
        System.out.println(manager.getSubtasksByEpicId(epic.getId()));



//        System.out.println(task1);
//        System.out.println(task2);
//
//        System.out.println(epic);
//
//        System.out.println(subTask1);
//        System.out.println(subTask3);
//        System.out.println(subTask4);
//        System.out.println(subTask5);
//        System.out.println(epic2);
//        System.out.println(manager.getAllSubtasks());




    }
}
