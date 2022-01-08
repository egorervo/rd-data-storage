package rd.core;

import lombok.SneakyThrows;
import rd.api.StorageClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DbServer {
    private final StorageClient storageClient;

    public DbServer(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @SneakyThrows
    public void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(6868);

        Socket socket = serverSocket.accept();

        final InputStream inputStream = socket.getInputStream();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

        while (true) {
            final String line = bufferedReader.readLine();
            final String[] s = line.split(" ");
            switch (s[0]) {
                case "set": {
                    if (s.length < 3) {
                        sendIncorrectArgumentsMessage(printWriter);
                    } else {
                        final String value = buildString(s, 2);
                        if (this.storageClient.put(s[1], value)) {
                            printWriter.println("%s inserted.".formatted(value));
                        } else {
                            printWriter.println("Fail");
                        }
                    }
                    break;
                }
                case "get": {
                    if (s.length < 2) {
                        sendIncorrectArgumentsMessage(printWriter);
                    } else {
                        printWriter.println(this.storageClient.get(s[1]));
                    }
                    break;
                }
                default:
                    printWriter.println("Unknown command");
            }
            if (null != line) {
                System.out.println("Line is %s".formatted(line));
                printWriter.println("This is response for " + line);
            }
        }
    }

    private void sendIncorrectArgumentsMessage(PrintWriter printWriter) {
        printWriter.println("Incorrect arguments");
    }

    private String buildString(String[] arr, int idxStart) {
        List<String> parts = new ArrayList<>();
        for (int i = idxStart; i < arr.length; i++) {
            parts.add(arr[i]);
        }
        return parts.stream().collect(Collectors.joining(" "));
    }
}
