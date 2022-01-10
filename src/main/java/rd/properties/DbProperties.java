package rd.properties;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DbProperties {
    private String storageFilePath;
    private int maxConnections;
    private int port;
}
