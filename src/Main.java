public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Epic epic1 = new Epic("Магазин","Сходить за покупками");
        Epic epic2 = new Epic("Школа","Собрать портфель");

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        SubTask subTask1 = new SubTask("Яблоки","2КГ", epic1.getId());
        SubTask subTask2 = new SubTask("Вода","3Л", epic1.getId());
        SubTask subTask3 = new SubTask("Масло","200г", epic1.getId());

        SubTask subTask4 = new SubTask("Линейка","1 Отдел", epic2.getId());
        SubTask subTask5 = new SubTask("Ручка","1 Отдел", epic2.getId());
        SubTask subTask6 = new SubTask("Тетрадь","2 Отдел", epic2.getId());

        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createSubtask(subTask3);

        manager.createSubtask(subTask4);
        manager.createSubtask(subTask5);
        manager.createSubtask(subTask6);

        System.out.println(epic1);
        System.out.println();
        System.out.println(epic2);
        System.out.println();

        manager.deleteAllSubtasks();
        System.out.println();

        System.out.println(epic1);
        System.out.println();
        System.out.println(epic2);
        System.out.println();
        System.out.println(manager.getAllSubtasks());


        }
}
