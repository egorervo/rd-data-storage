package rd.storage;

import lombok.SneakyThrows;
import rd.api.StorageClient;
import rd.properties.DbProperties;
import rd.util.PropertyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

public class RdStorageFilesStrategy implements StorageClient {
    private static final String FILES_DIR = "data";
    private final DbProperties dbProperties = PropertyUtils.getDbProperties();
    final File dataDir = new File(dbProperties.getStorageFilePath() + "/" + FILES_DIR);


    public RdStorageFilesStrategy() {
        this.createFilesDirIfNotExists();
    }

    @Override
    public boolean put(String key, String value) {
        try {
            Files.write(getDataFilePath(key),
                    value.getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
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
        try {
            Files.delete(getDataFilePath(key));
        } catch (Exception e) {
            System.out.println("Can not remove data by key=%s".formatted(key));
        }
    }

    @Override
    public String get(String key) {
        try {
            return Files.lines(getDataFilePath(key))
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.out.println("Can not get data for key=%s".formatted(key));
            e.printStackTrace();
            return null;
        }
    }

    private Path getDataFilePath(String key) {
        return Paths.get(dbProperties.getStorageFilePath(), FILES_DIR, key);
    }

    @SneakyThrows
    private void createFilesDirIfNotExists() {
        final File dbDir = new File(dbProperties.getStorageFilePath());
        if (!dbDir.exists()) {
            dbDir.mkdir();
        }
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }
}
