package works.hop.reducer.persist;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import works.hop.reducer.state.AbstractReducer;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.State;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class RedisReducer<S> extends AbstractReducer<Map<String, Map<String, S>>> implements Crud {

    public static final String NEXT_ID_KEY = "next_red_store_id";
    public static final String FETCH_ALL = "FETCH_ALL";
    public static final String CREATE_RECORD = "CREATE_RECORD";
    public static final String DELETE_RECORD = "DELETE_RECORD";
    public static final String UPDATE_RECORD = "UPDATE_RECORD";

    private final JedisPool pool;
    private final Kryo kryo;

    public RedisReducer(JedisPool pool, String name, Map<String, Map<String, S>> initialState) {
        super(name, initialState);
        this.pool = pool;
        this.kryo = new Kryo();
        this.kryo.register(RecordValue.class);
    }

    private static byte[] encode(Kryo kryo, Object obj) {
        ByteArrayOutputStream objStream = new ByteArrayOutputStream();
        Output objOutput = new Output(objStream);
        kryo.writeClassAndObject(objOutput, obj);
        objOutput.close();
        return objStream.toByteArray();
    }

    private static <T> T decode(Kryo kryo, byte[] bytes) {
        return (T) kryo.readClassAndObject(new Input(bytes));
    }

    private static String deriveUserCollectionName(RecordKey key) {
        return String.format("%s:%s", key.getUserKey(), key.getCollectionKey());
    }

    private synchronized Long nextId() {
        try (Jedis jedis = pool.getResource()) {
            Long id = Long.parseLong(Optional.ofNullable(jedis.get(NEXT_ID_KEY)).orElse("0"));
            jedis.set(NEXT_ID_KEY, Long.toString(id + 1));
            return id;
        }
    }

    @Override
    public List<RecordValue> fetch(RecordKey key) {
        try (Jedis jedis = pool.getResource()) {
            synchronized (kryo) {
                String userCollection = deriveUserCollectionName(key);
                byte[] collection = encode(kryo, userCollection);
                Map<byte[], byte[]> map = jedis.hgetAll(collection);
                return map.values().stream().map(bytes -> (RecordValue) decode(kryo, bytes)).collect(Collectors.toList());
            }
        }
    }

    @Override
    public long save(RecordEntity record) {
        Long id = nextId();
        record.getValue().setId(id);
        record.getValue().setId(id);
        try (Jedis jedis = pool.getResource()) {
            synchronized (kryo) {
                String userCollection = deriveUserCollectionName(record.getKey());
                byte[] collection = encode(kryo, userCollection);
                byte[] recordId = encode(kryo, id.toString());
                byte[] recordValue = encode(kryo, record.getValue());
                //add record to collection
                jedis.hset(collection, recordId, recordValue);
            }
        }
        return id;
    }

    @Override
    public int update(RecordEntity record) {
        Long id = record.getKey().getId();
        try (Jedis jedis = pool.getResource()) {
            synchronized (kryo) {
                String userCollection = deriveUserCollectionName(record.getKey());
                byte[] collection = encode(kryo, userCollection);
                byte[] recordId = encode(kryo, id.toString());
                byte[] recordValue = encode(kryo, record.getValue());
                //update record in collection
                jedis.hset(collection, recordId, recordValue);
                return 1;
            }
        }
    }

    @Override
    public int delete(RecordKey key) {
        Long id = key.getId();
        try (Jedis jedis = pool.getResource()) {
            synchronized (kryo) {
                String userCollection = deriveUserCollectionName(key);
                byte[] collection = encode(kryo, userCollection);
                byte[] recordId = encode(kryo, id.toString());
                //delete record from collection
                jedis.hdel(collection, recordId);
                return 1;
            }
        }
    }

    @Override
    public State<Map<String, Map<String, S>>> reduce(State<Map<String, Map<String, S>>> state, Action action) {
        switch (action.getType().get()) {
            case FETCH_ALL:
                RecordKey toFetch = (RecordKey) action.getBody();
                List<RecordValue> records = fetch(toFetch);
                Map<String, Map<String, S>> fetchedState = state.get();
                fetchedState.putIfAbsent(toFetch.getUserKey(), new HashMap<>());
                fetchedState.get(toFetch.getUserKey()).putIfAbsent(toFetch.getCollectionKey(), (S) records);
                return () -> fetchedState;
            case CREATE_RECORD:
                RecordEntity toCreate = (RecordEntity) action.getBody();
                long added = save(toCreate);
                if (added > 0) { //if created successfully
                    //add empty collections if need be
                    if (state.get().get(toCreate.getKey().getUserKey()) == null) {
                        Map<String, S> collections = new HashMap<>();
                        collections.put(toCreate.getKey().getCollectionKey(), (S) new ArrayList<>());
                        state.get().put(toCreate.getKey().getUserKey(), collections);
                    }
                    S list = state.get().get(toCreate.getKey().getUserKey()).get(toCreate.getKey().getCollectionKey());
                    List<RecordValue> values = (List<RecordValue>) list;
                    RecordValue addedValue = toCreate.getValue();
                    addedValue.setId(added);
                    values.add(addedValue);
                    //replace value in map
                    Map<String, Map<String, S>> newState = state.get();
                    newState.get(toCreate.getKey().getUserKey()).put(toCreate.getKey().getCollectionKey(), (S) values);
                    return () -> newState;
                }
                return () -> state.get();
            case DELETE_RECORD:
                RecordKey toDelete = (RecordKey) action.getBody();
                int deleted = delete(toDelete);
                if (deleted == 1) { //if deleted successfully
                    S list = state.get().get(toDelete.getUserKey()).get(toDelete.getCollectionKey());
                    List<RecordValue> values = ((List<RecordValue>) list).stream().filter(item -> !item.getId().equals(toDelete.getId())).collect(Collectors.toList());
                    //replace value in map
                    Map<String, Map<String, S>> newState = state.get();
                    newState.get(toDelete.getUserKey()).put(toDelete.getCollectionKey(), (S) values);
                    return () -> newState;
                }
                return () -> state.get();
            case UPDATE_RECORD:
                RecordEntity toUpdate = (RecordEntity) action.getBody();
                int updated = update(toUpdate);
                if (updated == 1) { //if updated successfully
                    S list = state.get().get(toUpdate.getKey().getUserKey()).get(toUpdate.getKey().getCollectionKey());
                    List<RecordValue> values = ((List<RecordValue>) list).stream().map(item -> {
                        if (item.getId().equals(toUpdate.getKey().getId())) {
                            return toUpdate.getValue();
                        }
                        return item;
                    }).collect(Collectors.toList());
                    //replace value in map
                    Map<String, Map<String, S>> newState = state.get();
                    newState.get(toUpdate.getKey().getUserKey()).put(toUpdate.getKey().getCollectionKey(), (S) values);
                    return () -> newState;
                }
                return () -> state.get();
            default:
                return () -> state.get();
        }
    }
}