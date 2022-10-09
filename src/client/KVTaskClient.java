package client;

import exception.HandlerRequestException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {
    private final URI uri;
    private final String apiToken;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public KVTaskClient(URI kvServerUri) {
        this.uri = kvServerUri;
        apiToken = register(uri);
    }

    private String register(URI serverUri) {
        try {
            URI registerUrl = URI.create(serverUri + "register");

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(registerUrl)
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> send = httpClient.send(request, handler);

            if(send.statusCode() != 200) {
                throw new HandlerRequestException("Запрос не был обработан. Код ответ: " + send.statusCode());
            }

            return send.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Ошибка регистрации.");
        }
    }

    public void put(String key, String json) {
        try {
            URI putUrl = URI.create(uri + "save/" + key + "?API_TOKEN=" + apiToken);

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(putUrl)
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> send = httpClient.send(request, handler);

            if(send.statusCode() != 200) {
                throw new HandlerRequestException("Запрос не был обработан. Код ответ: " + send.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Ошибка регистрации.");
        }
    }

    public String load(String key) {
        try {
            URI putUrl = URI.create(uri + "load/" + key + "?API_TOKEN=" + apiToken);

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(putUrl)
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> send = httpClient.send(request, handler);

            if(send.statusCode() != 200) {
                throw new HandlerRequestException("Запрос не был обработан. Код ответ: " + send.statusCode());
            }

            return send.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Ошибка регистрации.");
        }
    }
}