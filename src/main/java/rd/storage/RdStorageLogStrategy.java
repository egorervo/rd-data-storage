package rd.storage;

import lombok.SneakyThrows;
import rd.api.StorageClient;
import rd.properties.DbProperties;
import rd.util.PropertyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class RdStorageLogStrategy implements StorageClient {
    private static final String DB_FILE_LOG = "rd_db.log";
    private final DbProperties dbProperties = PropertyUtils.getDbProperties();
    final File logFile = new File(dbProperties.getStorageFilePath() + "/" + DB_FILE_LOG);


    public RdStorageLogStrategy() {
        this.createLogFileIfNotExists();
    }

    @Override
    public boolean put(String key, String value) {
        try {
            Files.write(logFile.toPath(), "%s,%s\n".formatted(key, value).getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean edit(String key, String value) {
        final String valueInDb = get(key);
        if (null == valueInDb) {
            return false;
        }
        return put(key, value);
    }

    @Override
    public void remove(String key) {
        put(key, "null");
    }

    @Override
    public String get(String key) {
        try {
            return Files.lines(logFile.toPath())
                    .filter(s -> s.startsWith(key + ","))
                    .reduce((f, s) -> s)
                    .map(k -> k.substring(key.length() + 1))
                    .filter(v -> !"null".equals(v))
                    .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SneakyThrows
    private void createLogFileIfNotExists() {
        final File dbDir = new File(dbProperties.getStorageFilePath());
        if (!dbDir.exists()) {
            dbDir.mkdir();
        }
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
    }
}
