package works.hop.reducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import works.hop.reducer.config.PersistTestConfig;
import works.hop.reducer.persist.JdbcObserver;
import works.hop.reducer.persist.JdbcState;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.persist.RecordKey;
import works.hop.reducer.state.DefaultStore;
import works.hop.reducer.state.State;
import works.hop.reducer.state.Store;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static works.hop.reducer.TodoListReducer.INIT_LIST;
import static works.hop.reducer.TodoListWebApp.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistTestConfig.class, loader = AnnotationConfigContextLoader.class)
//@Ignore("use only for integration testing")
public class TodoReducerTest {

    final String TODO_LIST = "TODO_LIST";
    final Store store = new DefaultStore();
    final ObjectMapper mapper = new ObjectMapper();
    final Logger LOGGER = LoggerFactory.getLogger(TodoReducerTest.class);
    @Autowired
    @Qualifier("localDS")
    DataSource dataSource;
    String userKey = "steve";

    @Before
    public void init() {
        JdbcObserver observer = new JdbcObserver();
        State<List<Todo>> states = new JdbcState<>(dataSource);
        //create reducer
        store.reducer(TODO_LIST, new TodoListReducer(TODO_LIST, states));
        store.subscribe(TODO_LIST, observer);
        store.state().forEach(state -> LOGGER.info("updated state - {}", state));
    }

    @Test
    public void test_INIT_LIST() throws JsonProcessingException {
        String userKey = "steve";
        TodoList scrum = TodoList.builder().todos(new ArrayList<>()).build();
        RecordEntity recordEntity = RecordEntity.builder().key(RecordKey.builder().userKey(userKey)
                .collectionKey(INIT_LIST).build())
                .value(mapper.writeValueAsBytes(scrum)).build();
        //dispatch reduce request
        store.dispatchAsync(initTodoList.apply(recordEntity), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                TodoList result = mapper.readValue(entity.getValue(), TodoList.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_CREATE_RECORD() throws JsonProcessingException {
        String recordId = "a799e81b-8500-4176-8edb-b55507546620";
        Todo todo = Todo.builder().id(recordId).task("buy milk").completed(false).build();
        //dispatch reduce request
        store.dispatchAsync(createRecord.apply(todo), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                TodoList result = mapper.readValue(entity.getValue(), TodoList.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_FETCH_ALL_RECORDS() {
        String recordId = "a799e81b-8500-4176-8edb-b55507546620";
        RecordKey criteria = RecordKey.builder().recordId(recordId).collectionKey(TODO_LIST).userKey(userKey).build();
        store.dispatchQuery(fetchAllRecords.apply(criteria), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                TodoList result = mapper.readValue(entity.getValue(), TodoList.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_UPDATE_RECORD() throws JsonProcessingException {
        String recordId = "a799e81b-8500-4176-8edb-b55507546620";
        Todo todo = Todo.builder().id(recordId).task("buy milk").completed(true).build();
        store.dispatchAsync(updateRecord.apply(todo), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                TodoList result = mapper.readValue(entity.getValue(), TodoList.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_DELETE_RECORD() {
        String recordId = "a799e81b-8500-4176-8edb-b55507546620";
        Todo todo = Todo.builder().id(recordId).task("buy milk").build();
        store.dispatchAsync(deleteRecord.apply(todo), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                TodoList result = mapper.readValue(entity.getValue(), TodoList.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testCompute() {
        System.out.println("COMPUTE test");
    }
}