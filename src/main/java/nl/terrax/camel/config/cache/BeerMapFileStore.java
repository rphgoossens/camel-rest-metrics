package nl.terrax.camel.config.cache;

import com.hazelcast.core.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Profile("!test")
@Component
public class BeerMapFileStore implements MapStore<String, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeerMapFileStore.class);

    private final String mapFileStoresDirectory;

    public BeerMapFileStore(@Value("${hazelcast.filestore.directory}") String mapFileStoresDirectory) {
        this.mapFileStoresDirectory = mapFileStoresDirectory;
    }

    @Override
    public synchronized void store(String key, Integer value) {
        updateFileStore(this.mapFileStoresDirectory + key, value);
    }

    @Override
    public void storeAll(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());
    }

    @Override
    public void delete(String key) {
        if ((Paths.get(this.mapFileStoresDirectory + key).toFile().exists())) {
            deleteFileStore(this.mapFileStoresDirectory + key);
        }
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        for (String key : keys) delete(key);
    }

    @Override
    public synchronized Integer load(String key) {
        if (!(Paths.get(this.mapFileStoresDirectory + key).toFile().exists())) {
            updateFileStore(this.mapFileStoresDirectory + key, 0);
            return 0;
        } else {
            return getValueFromFileStore(this.mapFileStoresDirectory + key);
        }
    }

    @Override
    public Map<String, Integer> loadAll(Collection<String> keys) {
        final Map<String, Integer> result = new HashMap<>();
        for (String key : keys) result.put(key, load(key));

        return result;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        try (Stream<Path> stores = Files.list(Paths.get(mapFileStoresDirectory))) {
            return stores.map(path -> path.getFileName().toString()).collect(Collectors.toList());
        } catch (IOException e) {
            throw new MapStoreException(e);
        }
    }

    private Integer getValueFromFileStore(String fileStore) {
        try {
            return ByteBuffer.wrap(Files.readAllBytes(Paths.get(fileStore))).getInt();
        } catch (IOException e) {
            throw new MapStoreException(e);
        }
    }

    private void updateFileStore(String fileStore, Integer value) {
        LOGGER.debug("Updating filestore: {}", fileStore);
        try {
            Files.write(Paths.get(fileStore), ByteBuffer.allocate(4).putInt(value).array());
        } catch (IOException e) {
            throw new MapStoreException(e);
        }
    }

    private void deleteFileStore(String fileStore) {
        LOGGER.debug("Deleting filestore: {}", fileStore);
        try {
            Files.delete(Paths.get(fileStore));
        } catch (IOException e) {
            throw new MapStoreException(e);
        }
    }

}
