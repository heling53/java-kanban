package manager.impl;

import manager.HistoryManager;
import tasks.AbstractTask;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int SIZE = 10;
    private final List<AbstractTask> history = new ArrayList<>();

    @Override
    public boolean add(AbstractTask task) {
        if (history.size() >= SIZE && task != null) {
            history.removeFirst();
        }
        return history.add(task);
    }

    @Override
    public String toString() {
        return history.toString();
    }

    @Override
    public List<AbstractTask> getHistory() {
        return new ArrayList<>(history);
    }

}
