package manager;

import tasks.AbstractTask;

public class Node {
    public AbstractTask task;
    public Node prev;
    public Node next;

    public Node(AbstractTask task, Node prev, Node next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }
}