package rd.server;

import lombok.SneakyThrows;
import rd.api.StorageClient;
import rd.util.PropertyUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DbServer {
    public static final int MAX_CONNECTIONS = PropertyUtils.getDbProperties().getMaxConnections();
    private final StorageClient storageClient;
    private final ExecutorService connectionPool = Executors.newFixedThreadPool(MAX_CONNECTIONS);

    public DbServer(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @SneakyThrows
    public void startServer() {
        ServerSocket serverSocket = new ServerSocket(PropertyUtils.getDbProperties().getPort());
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            connectionPool.submit(() -> {
                listen(serverSocket);
            });
        }
    }

    @SneakyThrows
    private void listen(ServerSocket serverSocket) {
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
                case "del": {
                    if (s.length < 2) {
                        sendIncorrectArgumentsMessage(printWriter);
                    } else {
                        this.storageClient.remove(s[1]);
                        printWriter.println("%s removed.".formatted(s[1]));
                    }
                    break;
                }
                case "update": {
                    if (s.length < 3) {
                        sendIncorrectArgumentsMessage(printWriter);
                    } else {
                        final String value = buildString(s, 2);
                        final boolean result = this.storageClient.edit(s[1], value);
                        printWriter.println(result ? "updated" : "not updated");
                    }
                    break;
                }
                default:
                    printWriter.println("Unknown command");
            }
            if (null != line) {
                System.out.println("Line is %s".formatted(line));
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
