import rd.server.DbServer;
import rd.storage.RdStorageFilesStrategy;

public class Main {
    public static void main(String[] args) {
        new DbServer(new RdStorageFilesStrategy()).startServer();
    }
}
