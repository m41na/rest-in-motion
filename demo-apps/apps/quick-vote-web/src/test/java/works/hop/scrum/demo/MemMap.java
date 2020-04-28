package works.hop.scrum.demo;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemMap extends HashMap<String, Object> {

    private static final AtomicLong GLOBAL_ID = new AtomicLong(0);

    public MemMap() {
        super();
    }

    public MemMap(String[] collections) {
        super();
        if (collections != null) {
            for (String collection : collections) {
                addCollection(collection);
            }
        }
    }

    public void addCollection(String name) {
        putIfAbsent(name, new MemMap());
    }

    public MemMap getCollection(String name) {
        return get(name, MemMap.class);
    }

    public <T> String save(String collection, T entity) {
        String id = Long.valueOf(nextId()).toString();
        getCollection(collection).putIfAbsent(id, entity);
        return id;
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(get(key));
    }

    public Long nextId() {
        return GLOBAL_ID.incrementAndGet();
    }
}
