import adapters.InstantAdapter;
import client.HTTPTaskManager;
import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import server.HttpTaskServer;
import server.KVServer;
import status.Status;
import tasks.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {
        // Спринт 8
        try {
            //Server
            new KVServer().start();
            HttpTaskServer httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();

            // Client
//            HistoryManager historyManager = Managers.getDefaultHistory();
//            HTTPTaskManager httpTaskManager = Managers.getDefault(historyManager);
//            Task firstTask = new Task(
//                    "Разработать лифт до луны", "Космолифт",
//                    Status.NEW,
//                    Instant.now(),
//                    1
//            );
            //httpTaskManager.createTask(firstTask);
            //System.out.println(httpTaskManager);

//            URI baseUrl = URI.create("http://localhost:" + KVServer.PORT);
//            KVTaskClient client = new KVTaskClient(baseUrl);
//            String jsonTask = "[{description='Разработать лифт до луны', name='Космолифт', status=new'," +
//                    " startTime='1665298059212', endTime='1665298119212', duration='1}]";
//            client.put("tasks", jsonTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
