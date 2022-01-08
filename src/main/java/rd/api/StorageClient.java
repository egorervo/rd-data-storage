package rd.api;

public interface StorageClient {

    boolean put(String key, String value);

    boolean edit(String key, String value);

    void remove(String key);

    String get(String key);
}
