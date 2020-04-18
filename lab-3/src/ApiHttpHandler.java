import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;

public class ApiHttpHandler implements HttpHandler {
    private static final String FILES_FOLDER_NAME = "D:\\University\\4 semester\\KSiS\\lab-3\\src\\Files\\";
    private static HashMap<Integer, File> filesMap = new HashMap<Integer, File>();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        switch (requestMethod) {
            case "POST":
                handlePostRequest(httpExchange);
                break;
            case "HEAD":
                handleHeadRequest(httpExchange);
                break;
            case "GET":
                handleGetRequest(httpExchange);
                break;
            case "DELETE":
                handleDeleteRequest(httpExchange);
                break;
            default:
                handleUnrecognizedRequest(httpExchange);
                System.out.println("Unrecognized request method:" + httpExchange.getRequestMethod());
        }
        httpExchange.close();
    }

    private void handleUnrecognizedRequest(HttpExchange httpExchange) {
        try {
            httpExchange.sendResponseHeaders(501, 0);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.flush();
        } catch (IOException ex) {
            System.out.println(this.toString() + ": " + ex.getMessage());
        }
    }

    private void handleDeleteRequest(HttpExchange httpExchange) {
        try {
            httpExchange.sendResponseHeaders(501, 0);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.flush();
        } catch (IOException ex) {
            System.out.println(this.toString() + ": " + ex.getMessage());
        }
    }

    private void handleGetRequest(HttpExchange httpExchange) {
        OutputStream outputStream = httpExchange.getResponseBody();
        String response = "Response to GET method with URI: " + httpExchange.getRequestURI();
        try {
            httpExchange.sendResponseHeaders(200, response.length());
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException ex) {
            System.out.println(this.toString() + ": " + ex.getMessage());
        }
    }

    private void handleHeadRequest(HttpExchange httpExchange) {
        try {
            httpExchange.sendResponseHeaders(501, 0);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.flush();
        } catch (IOException ex) {
            System.out.println(this.toString() + ": " + ex.getMessage());
        }
    }

    private void handlePostRequest(HttpExchange httpExchange) {
        InputStream inputStream = httpExchange.getRequestBody();
        OutputStream outputStream = httpExchange.getResponseBody();
        try {
            String fileData = new String(inputStream.readAllBytes());
            String fileName = httpExchange.getRequestURI().toString().split("/")[2];
            int fileID = addFileToStorage(fileName, fileData);
            httpExchange.sendResponseHeaders(200, String.valueOf(fileID).length());
            outputStream.write(String.valueOf(fileID).getBytes());
            outputStream.flush();
        } catch (Exception ex) {
            System.out.println(this.toString() + ": " + ex.getMessage());
            try {
                httpExchange.sendResponseHeaders(400, ex.getMessage().length());
                outputStream.write(ex.getMessage().getBytes());
                outputStream.flush();
            } catch (IOException io) {
                System.out.println(this.toString() + ": " + io.getMessage());
            }
        }
    }

    public int addFileToStorage(String fileName, String fileData) throws FileAlreadyExistsException, IOException {
        File fileObj = new File(FILES_FOLDER_NAME + fileName);
        if (fileObj.exists()) {
            throw new FileAlreadyExistsException("File already exists: " + fileName);
        } else {
            boolean wasCreated = fileObj.createNewFile();
            if (wasCreated) {
                FileWriter fileWriter = new FileWriter(fileObj);
                fileWriter.write(fileData);
                fileWriter.flush();
                fileWriter.close();
            }
        }
        filesMap.put(fileObj.hashCode(), fileObj);
        return fileObj.hashCode();
    }
}
