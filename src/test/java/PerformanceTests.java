import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import rd.api.StorageClient;
import rd.properties.DbProperties;
import rd.storage.RdStorageFilesStrategy;
import rd.storage.RdStorageLogStrategy;
import rd.util.PropertyUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PerformanceTests {
    final DbProperties dbProperties = PropertyUtils.getDbProperties();

    @Test
    public void performanceTest() {
        cleanDbDirectories();
        StorageClient rdStorageLogStrategy = new RdStorageLogStrategy();

        final int operationsCount = 100000;
        final double averageWriteItemTime = testWrite(operationsCount, rdStorageLogStrategy);
        final double averageReadItemTime = testRead(operationsCount, rdStorageLogStrategy);
        final double averageEditItemTime = testEdit(operationsCount, rdStorageLogStrategy);
        final double averageDeleteItemTime = testDelete(operationsCount, rdStorageLogStrategy);

        cleanDbDirectories();
        StorageClient rdStorageFilesStrategy = new RdStorageFilesStrategy();
        final double averageWriteItemTimeFilesStr = testWrite(operationsCount, rdStorageFilesStrategy);
        final double averageReadItemTimeFilesStr = testRead(operationsCount, rdStorageFilesStrategy);
        final double averageEditItemTimeFilesStr = testEdit(operationsCount, rdStorageFilesStrategy);
        final double averageDeleteItemTimeFilesStr = testDelete(operationsCount, rdStorageFilesStrategy);

        System.out.println("""
                TEST RESULTS 
                Log strategy:
                average write item time = %sms
                average read item time = %sms
                average edit item time = %sms
                average delete item time = %sms
                ======================
                
                Files strategy:
                average write item time = %sms
                average read item time = %sms
                average edit item time = %sms
                average delete item time = %sms
                """.formatted(averageWriteItemTime,
                averageReadItemTime,
                averageEditItemTime,
                averageDeleteItemTime,
                averageWriteItemTimeFilesStr,
                averageReadItemTimeFilesStr,
                averageEditItemTimeFilesStr,
                averageDeleteItemTimeFilesStr));
    }

    @SneakyThrows
    public void cleanDbDirectories() {
        try {
            Files.walk(Paths.get(dbProperties.getStorageFilePath()))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (NoSuchFileException e) {
            System.out.println("No data to remove");
        }
    }

    public double testWrite(int entriesCount, StorageClient storageClient) {
        List<Long> resultsTime = new ArrayList<>();
        for (int i = 0; i < entriesCount; i++) {
            if (i % 10000 == 0) {
                System.out.println("iteration write " + i);
            }
            final long start = System.currentTimeMillis();
            storageClient.put("key" + i, "value" + i);
            resultsTime.add(System.currentTimeMillis() - start);
        }
        return resultsTime.stream().mapToLong(a -> a).average().orElse(0);
    }

    public double testEdit(int entriesCount, StorageClient storageClient) {
        List<Long> resultsTime = new ArrayList<>();
        for (int i = 0; i < entriesCount; i++) {
            if (i % 10000 == 0) {
                System.out.println("iteration edit " + i);
            }
            final long start = System.currentTimeMillis();
            storageClient.edit("key" + i, "value editied" + i);
            resultsTime.add(System.currentTimeMillis() - start);
        }
        return resultsTime.stream().mapToLong(a -> a).average().orElse(0);
    }

    public double testRead(int entriesCount, StorageClient storageClient) {
        List<Long> resultsTime = new ArrayList<>();
        for (int i=0; i<entriesCount; i++) {
            if (i % 1000 == 0) {
                System.out.println("iteration read " + i);
            }
            final long start = System.currentTimeMillis();
            storageClient.get("key" + i);
            resultsTime.add(System.currentTimeMillis() - start);
        }
        return resultsTime.stream().mapToLong(a -> a).average().orElse(0);
    }

    public double testDelete(int entriesCount, StorageClient storageClient) {
        List<Long> resultsTime = new ArrayList<>();
        for (int i=0; i<entriesCount; i++) {
            if (i % 1000 == 0) {
                System.out.println("iteration delete " + i);
            }
            final long start = System.currentTimeMillis();
            storageClient.remove("key" + i);
            resultsTime.add(System.currentTimeMillis() - start);
        }
        return resultsTime.stream().mapToLong(a -> a).average().orElse(0);
    }
}
