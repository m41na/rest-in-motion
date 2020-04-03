package works.hop.reducer.persist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.JedisPool;
import works.hop.reducer.Todo;
import works.hop.reducer.config.PersistTestConfig;
import works.hop.reducer.state.ActionCreator;
import works.hop.reducer.state.DefaultStore;
import works.hop.reducer.state.Store;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static works.hop.reducer.persist.RedisReducer.DELETE_RECORD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistTestConfig.class)
public class RedisReducerITest {

    @Autowired
    JedisPool pool;

    String key = "todos";
    String user = "steve";
    String collection = "todos";

    ActionCreator creator = new ActionCreator();
    RedisReducer<List<Todo>> reducer;
    Store store;
    RedisObserver observer;

    @Before
    public void setUp() {
        reducer = new RedisReducer<>(pool, key, new HashMap<>());
        observer = new RedisObserver();
        store = new DefaultStore();
        store.reducer(key, reducer);
        store.subscribe(key, observer);
    }

    @Test
    public void fetchAll() {
        List<RecordValue> list = reducer.fetch(RecordKey.builder().userKey(user).collectionKey(collection).build());
        assertTrue(list.size() == 1);
    }

    @Test
    public void save() {
        Todo todo = Todo.builder().task("bread").completed(false).build();
        RecordEntity record = RecordEntity.builder().key(RecordKey.builder().userKey(user).collectionKey(collection).build())
                .dateCreated(new Date()).value(todo).build();
        store.dispatch(creator.create(() -> "CREATE_RECORD").apply(record), state -> {
            List<Todo> list = state.getList(state.getMap(state.getMap(), user), collection);
            assertEquals(1, list.size());
        });
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
        store.dispatch(creator.create(() -> DELETE_RECORD).apply(RecordKey.builder().userKey(user).collectionKey(collection).id(4l).build()), state -> {
            List<Todo> list = state.getList(state.getMap(state.getMap(), user), collection);
            assertEquals(1, list.size());
        });
    }

    @Test
    public void reduce() {
    }
}