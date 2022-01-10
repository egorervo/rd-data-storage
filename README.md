### key-value storage

#### Start server
```java
    new DbServer(new RdStorageFilesStrategy()).startServer();
```
or
```java
    new DbServer(new RdStorageLogStrategy()).startServer();
```

#### USAGE
connect to DB server example
```bash
    telnet 127.0.0.1 6868
```
supported commands 
```bash
    set ${key} ${value}
    get ${key}
    del ${key}
    update ${key} ${value}
```
example
![](https://pc.stripocdn.email/content/guids/CABINET_882f5d4fd6a76a42e57671ddef5c4a36/images/myimage2.png)
#### Properties
```properties
    storage.path=./rd-storage-dir   #path where db files will be stored
    storage.max.connections=5       #max allowed server connections 
    storage.port=6868               #server port
```
#### Performance tests result
![](https://pc.stripocdn.email/content/guids/CABINET_882f5d4fd6a76a42e57671ddef5c4a36/images/myimage.png)
