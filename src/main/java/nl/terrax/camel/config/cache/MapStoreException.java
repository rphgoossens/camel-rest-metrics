package nl.terrax.camel.config.cache;

import java.io.IOException;

public class MapStoreException extends RuntimeException {
    public MapStoreException(IOException e) {
        super(e);
    }
}
