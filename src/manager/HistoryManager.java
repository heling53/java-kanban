package manager;

import tasks.AbstractTask;

import java.util.List;

public interface HistoryManager {
    //@Override
    boolean add(AbstractTask task);

    List<AbstractTask> getHistory();

    //void addToHistory(AbstractTask task);
}
