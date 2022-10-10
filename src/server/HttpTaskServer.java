package server;

import com.sun.net.httpserver.HttpServer;
import manager.FileBackedTasksManager;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import server.handlers.EpicHandler;
import server.handlers.SubtaskHandler;
import server.handlers.TaskHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final TaskManager taskManager;
    private final HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer() throws IOException, InterruptedException {
        HistoryManager historyManager = Managers.getDefaultHistory();
        this.taskManager = Managers.getDefault(historyManager);
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task/", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/epic/", new EpicHandler(taskManager));
        httpServer.createContext("/tasks/subtask/", new SubtaskHandler(taskManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

}
