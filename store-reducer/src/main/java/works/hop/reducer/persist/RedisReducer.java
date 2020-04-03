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
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RedisReducer<S> extends AbstractReducer<Map<String, Map<String, S>>> implements Crud<RecordValue> {

    public static final String NEXT_ID_KEY = "next_red_store_id";

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

    private static String createUserCollectionName(RecordKey key) {
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
        return null;
    }

    @Override
    public long save(RecordKey key, RecordValue record) {
        Long id = nextId();
        key.setId(id);
        record.setId(id);
        try (Jedis jedis = pool.getResource()) {
            synchronized (kryo) {
                String userCollectionName = createUserCollectionName(key);
                byte[] userCollection = encode(kryo, userCollectionName);
                byte[] recordId = encode(kryo, id.toString());
                byte[] recordsBytes = jedis.hget(userCollection, recordId);
                List<RecordValue> list = decode(kryo, recordsBytes);
                list.add(record);
                byte[] newRecordsBytes = encode(kryo, record);
                //save updated records
                jedis.hset(userCollection, recordId, newRecordsBytes);
            }
        }
        return id;
    }

    @Override
    public int update(RecordValue record) {
        return 0;
    }

    @Override
    public int delete(Long id) {
        return 0;
    }

    @Override
    public State<Map<String, Map<String, S>>> reduce(State<Map<String, Map<String, S>>> state, Action action) {
        return null;
    }
}