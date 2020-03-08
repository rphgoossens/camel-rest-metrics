package nl.terrax.camel.config;

import java.io.IOException;

public class MapStoreException extends RuntimeException {
    public MapStoreException(IOException e) {
        super(e);
    }
}
