package Client.FileStorageManager;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;

public class FileStorageManager {
    final String API_URL = "";

    public FileStorageManager() {

    }

    public void createClient() {}

    public void putFileToStorage(File file) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("license.codegear.com:7777/Files/" + file.getName()))
                    .timeout(Duration.ofMinutes(1))
                    .POST(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(System.out::println)
                    .join();
        } catch (Exception ex) {

        }
    }
}
