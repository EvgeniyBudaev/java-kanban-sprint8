import adapters.InstantAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import server.KVServer;
import status.Status;
import tasks.Task;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {
        // Спринт 8
        KVServer server;
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            TaskManager httpTaskManager = Managers.getDefault(historyManager);

            Task task1 = new Task(
                    "Разработать лифт до луны", "Космолифт",
                    Status.NEW,
                    Instant.now(),
                    1
            );
            httpTaskManager.createTask(task1);
            httpTaskManager.getTaskById(task1.getId());

            System.out.println("Печать всех задач");
            System.out.println(gson.toJson(httpTaskManager.getAllTasks()));
            System.out.println("Загруженный менеджер");
            System.out.println(httpTaskManager);
            server.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
