package works.hop.reducer.persist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import works.hop.reducer.Todo;
import works.hop.reducer.config.PersistTestConfig;
import works.hop.reducer.state.ActionCreator;
import works.hop.reducer.state.DefaultStore;
import works.hop.reducer.state.Store;

import javax.sql.DataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistTestConfig.class)
public class JdbcReducerITest {

    @Autowired
    DataSource dataSource;

    String key = "todos";
    String user = "steve";
    String collection = "todos";

    ActionCreator creator = new ActionCreator();
    JdbcReducer<List<Todo>> reducer;
    Store store;
    JdbcObserver observer;

    @Before
    public void setUp() {
        reducer = new JdbcReducer<>(dataSource, key, new HashMap<>());
        observer = new JdbcObserver();
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
    }

    @Test
    public void reduce() {
    }
}