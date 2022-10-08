import servers.HttpTaskServer;
import servers.KVServer;

public class Main {

    public static void main(String[] args) {
        // Спринт 8
        try {
            new KVServer().start();
            HttpTaskServer taskServer = new HttpTaskServer();
            taskServer.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
