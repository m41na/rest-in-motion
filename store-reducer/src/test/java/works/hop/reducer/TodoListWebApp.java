package works.hop.reducer;

import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import works.hop.core.JsonResult;
import works.hop.reducer.config.PersistTestConfig;
import works.hop.reducer.persist.JdbcObserver;
import works.hop.reducer.persist.JdbcState;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.persist.RecordKey;
import works.hop.reducer.state.*;

import javax.sql.DataSource;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static works.hop.jetty.JettyStartable.createServer;
import static works.hop.jetty.startup.AppOptions.applyDefaults;
import static works.hop.reducer.TodoListReducer.INIT_LIST;

public class TodoListWebApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoListWebApp.class);
    private static final ActionCreator actions = new ActionCreator();

    public static final Function<RecordEntity, Action<TodoList>> initTodoList = actions.create(() -> INIT_LIST);
    public static final Function<RecordKey, Action<TodoList>> fetchAllRecords = actions.create(() -> TodoListReducer.FETCH_ALL);
    public static final Function<Todo, Action<TodoList>> createRecord = actions.create(() -> TodoListReducer.CREATE_RECORD);
    public static final Function<Todo, Action<TodoList>> deleteRecord = actions.create(() -> TodoListReducer.DELETE_RECORD);
    public static final Function<Todo, Action<TodoList>> updateRecord = actions.create(() -> TodoListReducer.UPDATE_RECORD);

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(PersistTestConfig.class);
        DataSource remoteDataSource = ctx.getBean("remoteDS", DataSource.class);
        DataSource dataSource = ctx.getBean("localDS", DataSource.class);

        final String TODO_LIST = "TODO_LIST";
        JdbcObserver observer = new JdbcObserver();
        State<TodoList> states = new JdbcState<>(dataSource);

        //create reducer
        Store store = new DefaultStore();
        store.reducer(TODO_LIST, new TodoListReducer<>(TODO_LIST, states));
        store.subscribe(TODO_LIST, observer);
        store.state().forEach(state -> LOGGER.info("updated state - {}", state));

        //create rest api
        Map<String, String> properties = applyDefaults(new Options(), args);
        var app = createServer(properties.get("appctx"), properties, builder -> builder.cors(emptyMap())
                .sse("/sse", (config) -> config.setAsyncSupported(true), observer).build());
        app.before((req, res, done) -> System.out.println("PRINT BEFORE ALL /"));
        app.before("get", "/", (req, res, done) -> System.out.println("PRINT BEFORE GET /"));
        app.get("/", (req, res, done) -> done.resolve(() -> {
            res.send("TODO web app using jdbc reducer");
        }));
        app.get("/{user}", (req, res, done) -> {
            String userKey = req.param("user");
            RecordKey criteria = RecordKey.builder().collectionKey(TODO_LIST).userKey(userKey).build();
            done.resolve(store.dispatchQuery(fetchAllRecords.apply(criteria), state -> res.json(JsonResult.ok(state))));
        });
        app.post("/{listId}", "application/json", "application/json", (req, res, done) -> {
            String listId = req.param("listId");
            Todo todo = req.body(Todo.class);
            todo.id = listId;
            done.resolve(store.dispatchAsync(createRecord.apply(todo), state -> {
                RecordEntity entity = (RecordEntity) state;
                TodoListRecord record = TodoListRecord.builder().entity(entity).build();
                TodoList result = record.getValue();
                result.setResourceId(entity.getKey().getRecordId().toString());
                res.json(JsonResult.ok(result));
            }));
        });
        app.put("/{listId}", (req, res, done) -> {
            String listId = req.param("listId");
            Todo todo = req.body(Todo.class);
            todo.id = listId;
            done.resolve(store.dispatchAsync(updateRecord.apply(todo), state -> {
                RecordEntity entity = (RecordEntity) state;
                TodoListRecord record = TodoListRecord.builder().entity(entity).build();
                TodoList result = record.getValue();
                res.json(JsonResult.ok(result));
            }));
        });
        app.delete("/{listId}/{task}", (req, res, done) -> {
            String listId = req.param("listId");
            String task = req.param("task");
            Todo todo = Todo.builder().task(task).id(listId).build();
            done.resolve(store.dispatchAsync(deleteRecord.apply(todo), state -> {
                RecordEntity entity = (RecordEntity) state;
                TodoListRecord record = TodoListRecord.builder().entity(entity).build();
                TodoList result = record.getValue();
                res.json(JsonResult.ok(result));
            }));
        });
        app.after("get", "/", (req, res, done) -> System.out.println("PRINT AFTER GET /"));
        app.after((req, res, done) -> System.out.println("PRINT AFTER ALL /"));
        app.listen(8090, "localhost");
    }
}
