package manager;

import servers.HTTPTaskManager;
import servers.KVServer;

import java.io.IOException;

public class Managers {
    public static TaskManager getInMemoryTaskManager(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static TaskManager getDefault(HistoryManager historyManager) throws IOException, InterruptedException {
        return new HTTPTaskManager(historyManager, "http://localhost:" + KVServer.PORT);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
