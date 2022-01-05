package rd.storage;

public interface StorageClient {

    String put(String key, String value);

    String remove(String key);

    String get(String key);
}
