package nl.terrax.camel.config.cache;

import com.hazelcast.core.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Profile("!test")
@Component
public class BeerMapDBStore implements MapStore<String, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeerMapDBStore.class);

    public static final String CREATE_TABLE_STMT =
            "create table if not exists beer_summary" +
                    "( beer  varchar(255) not null primary key," +
                    "  total int null );";
    public static final String DELETE_STMT = "delete from beer_summary where beer = ?";
    public static final String INSERT_STMT = "insert into beer_summary(beer,total) values(?,?) on duplicate key update total=?";
    public static final String SELECT_STMT = "select total from beer_summary where beer = ?";

    private final JdbcTemplate jdbcTemplate;

    public BeerMapDBStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        jdbcTemplate.execute(CREATE_TABLE_STMT);
    }

    @Override
    public synchronized void delete(String key) {
        LOGGER.debug("Called delete {}", key);
        jdbcTemplate.update(DELETE_STMT, key);
    }

    @Override
    public synchronized void store(String key, Integer value) {
        LOGGER.debug("Called store {} with value {}", key, value);
        jdbcTemplate.update(INSERT_STMT, key, value, value);
    }

    @Override
    public synchronized void storeAll(Map<String, Integer> map) {
        LOGGER.debug("Called storeAll");
        for (Map.Entry<String, Integer> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());
    }

    @Override
    public synchronized void deleteAll(Collection<String> keys) {
        LOGGER.debug("Called deleteAll");
        for (String key : keys) delete(key);
    }

    @Override
    public synchronized Integer load(String key) {
        LOGGER.debug("Called load {}", key);
        try {
            return jdbcTemplate.queryForObject(
                    SELECT_STMT,
                    new Object[]{key},
                    Integer.class
            );
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Override
    public synchronized Map<String, Integer> loadAll(Collection<String> keys) {
        LOGGER.debug("Called loadAll");
        Map<String, Integer> result = new HashMap<>();
        for (String key : keys) result.put(key, load(key));
        return result;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        LOGGER.debug("Called loadAllKeys");
        return null;
    }
}

