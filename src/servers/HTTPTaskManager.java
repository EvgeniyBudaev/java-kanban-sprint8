package servers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import manager.FileBackedTasksManager;
import manager.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;

public class HTTPTaskManager extends FileBackedTasksManager {

    final static String KEY_TASKS = "tasks";
    final static String KEY_SUBTASKS = "subtasks";
    final static String KEY_EPICS = "epics";
    final static String KEY_HISTORY = "history";
    final static String KEY_NEXT_ID = "nextId";
    final KVTaskClient client;
    int nextId;

    public HTTPTaskManager(HistoryManager historyManager, String path) throws IOException, InterruptedException {
        super(historyManager);
        client = new KVTaskClient(path);
        Gson gson = new Gson();

        JsonElement jsonNextId = JsonParser.parseString(client.load(KEY_NEXT_ID));
        nextId = jsonNextId.getAsInt();

        JsonElement jsonTasks = JsonParser.parseString(client.load(KEY_TASKS));
        JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
        for (JsonElement jsonTask : jsonTasksArray) {
            Task task = gson.fromJson(jsonTask, Task.class);
            this.addTask(task);
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load(KEY_SUBTASKS));
        JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
        for (JsonElement jsonSubtask : jsonSubtasksArray) {
            Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
            this.addSubtask(subtask);
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(KEY_EPICS));
        JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
        for (JsonElement jsonEpic : jsonEpicsArray) {
            Epic epic = gson.fromJson(jsonEpic, Epic.class);
            this.addEpic(epic);
        }

        JsonElement jsonHistoryList = JsonParser.parseString(client.load(KEY_HISTORY));
        JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
        for (JsonElement jsonTaskId : jsonHistoryArray) {
            int taskId = jsonTaskId.getAsInt();
            if (this.subtasks.containsKey(taskId)) {
                this.getSubtaskById(taskId);
            } else if (this.epics.containsKey(taskId)) {
                this.getEpicById(taskId);
            } else if (this.tasks.containsKey(taskId)) {
                this.getTaskById(taskId);
            }
        }
    }

    @Override
    public void save() {
        Gson gson = new Gson();
        client.put(KEY_NEXT_ID, gson.toJson(nextId));
        client.put(KEY_TASKS, gson.toJson(tasks));
        client.put(KEY_SUBTASKS, gson.toJson(subtasks));
        client.put(KEY_EPICS, gson.toJson(epics));
        client.put(KEY_HISTORY, gson.toJson(FileBackedTasksManager.historyToString(this.getHistoryManager())));
    }
}