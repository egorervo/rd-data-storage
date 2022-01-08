package rd.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import rd.properties.DbProperties;

import java.util.Properties;

@UtilityClass
public class PropertyUtils {
    private static DbProperties dbProperties;

    @SneakyThrows
    public DbProperties getDbProperties() {
        if (null == dbProperties) {
            Properties properties = new Properties();
            properties.load(DbProperties.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties"));
            dbProperties = DbProperties.builder()
                    .storageFilePath(properties.getProperty("storage.path"))
                    .build();
        }
        return dbProperties;
    }
}
