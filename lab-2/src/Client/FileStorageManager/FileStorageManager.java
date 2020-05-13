package Client.FileStorageManager;

import javax.imageio.IIOException;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileAlreadyExistsException;

public class FileStorageManager {
    final String API_URL = "http://license.codegear.com:7777/Files/";
    private int operationsInProgress = 0;

    private boolean isComplete() {
        return operationsInProgress == 0;
    }

    public void deleteFileFromStorage(UniqueFile file, Button sendButton) throws FileNotFoundException {
        operationsInProgress++;
        sendButton.setEnabled(false);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + file.getID()))
                .DELETE()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        operationsInProgress--;
                        if (isComplete()) {
                            sendButton.setEnabled(true);
                        }
                    };
                });
    }

    public void getFileFromStorage(UniqueFile file){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + file.getID()))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        File fileObj = new File(file.getOriginalName());
                        if (fileObj.exists()) {
                            System.out.println("File storage error: file already exists: " + file.getOriginalName());
                        } else {
                            try {
                                boolean wasCreated = fileObj.createNewFile();
                                if (wasCreated) {
                                    FileOutputStream fileWriter = new FileOutputStream(fileObj);
                                    fileWriter.write(response.body().getBytes());
                                    fileWriter.flush();
                                    fileWriter.close();
                                }
                            } catch (Exception ex) {
                                System.out.println("File storage error: " + ex.getMessage());
                            }
                        }
                    };
                });
    }

    public void putFileToStorage(File file, UniqueFile uniqueFile, Button sendButton) throws FileAlreadyExistsException {
        operationsInProgress++;
        sendButton.setEnabled(false);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + generateUniqueName()))
                    .POST(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            uniqueFile.setID(Integer.parseInt(response.body()));
                            operationsInProgress--;
                            if (isComplete()) {
                                sendButton.setEnabled(true);
                            }
                        };
                    });
        } catch (Exception ex) {
            System.out.println("File storage error: " + ex.getMessage());
        }
    }

    private static String generateUniqueName() {
        final int N = 20;
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(N);
        for (int i = 0; i < N; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
}
