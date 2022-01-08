import rd.api.StorageClient;
import rd.core.DbServer;
import rd.storage.RdStorage;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new DbServer(new RdStorage()).startServer();
    }
}
