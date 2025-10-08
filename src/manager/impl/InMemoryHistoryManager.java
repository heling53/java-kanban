package manager.impl;

import manager.HistoryManager;
import manager.Node;
import tasks.AbstractTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    private void linkLast(AbstractTask task) {
        Node oldTail = tail;
        Node newNode = new Node(task, oldTail, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        historyMap.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }
    }


    @Override
    public void add(AbstractTask task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        linkLast(task);
    }


    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "historyMap=" + historyMap +
                ", head=" + head +
                ", tail=" + tail +
                '}';
    }

    @Override
    public List<AbstractTask> getHistory() {
        List<AbstractTask> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.remove(id);
        if (node != null) {
            removeNode(node);
        }

    }

}
