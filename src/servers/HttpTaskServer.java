package servers;

import com.sun.net.httpserver.HttpServer;
import manager.FileBackedTasksManager;
import manager.Managers;
import servers.handlers.EpicHandler;
import servers.handlers.SubtaskHandler;
import servers.handlers.TaskHandler;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public class HttpTaskServer {
    //    private final TaskManager taskManager;
    private final FileBackedTasksManager taskManager;
    private final HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer() throws IOException {
        Path path = Path.of("data.csv");
        File file = new File(String.valueOf(path));
        this.taskManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        this.taskManager.loadFromFile();
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
