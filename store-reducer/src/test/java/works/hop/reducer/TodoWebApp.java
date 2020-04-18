package works.hop.reducer;

import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import works.hop.core.JsonResult;
import works.hop.reducer.config.PersistTestConfig;
import works.hop.reducer.persist.*;
import works.hop.reducer.state.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static works.hop.jetty.JettyStartable.createServer;
import static works.hop.jetty.startup.AppOptions.applyDefaults;

public class TodoWebApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoWebApp.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(PersistTestConfig.class);
        DataSource remoteDataSource = ctx.getBean("remoteDS", DataSource.class);
        DataSource dataSource = ctx.getBean("localDS", DataSource.class);

        final String TODO_LIST = "TODO_LIST";
        ActionCreator actions = new ActionCreator();
        JdbcObserver observer = new JdbcObserver();
        State<List<Todo>> states = new JdbcState<>(dataSource);

        //available actions
        Function<RecordKey, Action<List<Todo>>> fetchAllRecords = actions.create(() -> JdbcReducer.FETCH_ALL);
        Function<RecordEntity, Action<List<RecordEntity>>> createRecord = actions.create(() -> JdbcReducer.CREATE_RECORD);
        Function<RecordKey, Action<List<RecordEntity>>> deleteRecord = actions.create(() -> JdbcReducer.DELETE_RECORD);
        Function<RecordEntity, Action<List<RecordEntity>>> updateRecord = actions.create(() -> JdbcReducer.UPDATE_RECORD);

        //create reducer
        Store store = new DefaultStore();
        store.reducer(TODO_LIST, new JdbcReducer<>(TODO_LIST, states));
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
            done.resolve(store.dispatchAsync(fetchAllRecords.apply(criteria), state -> res.json(JsonResult.ok(state))));
        });
        app.post("/{user}", "application/json", "application/json", (req, res, done) -> {
            String userKey = req.param("user");
            Todo todo = req.body(Todo.class);
            RecordEntity recordEntity = RecordEntity.builder().key(RecordKey.builder().userKey(userKey)
                    .collectionKey(TODO_LIST).build())
                    .value(todo).build();
            done.resolve(store.dispatchAsync(createRecord.apply(recordEntity), state -> res.json(JsonResult.ok(state))));
        });
        app.put("/{user}", (req, res, done) -> {
            String userKey = req.param("user");
            Todo todo = req.body(Todo.class);
            RecordEntity recordEntity = RecordEntity.builder().key(RecordKey.builder().id(todo.id).userKey(userKey)
                    .collectionKey(TODO_LIST).build())
                    .value(todo).build();
            done.resolve(store.dispatchAsync(updateRecord.apply(recordEntity), state -> res.json(JsonResult.ok(state))));
        });
        app.delete("/{user}/{id}", (req, res, done) -> {
            String userKey = req.param("user");
            Long todoId = req.longParam("id");
            RecordKey deleteId = RecordKey.builder().id(todoId).userKey(userKey).collectionKey(TODO_LIST).build();
            done.resolve(store.dispatchAsync(deleteRecord.apply(deleteId), state -> res.json(JsonResult.ok(state))));
        });
        app.after("get", "/", (req, res, done) -> System.out.println("PRINT AFTER GET /"));
        app.after((req, res, done) -> System.out.println("PRINT AFTER ALL /"));
        app.listen(8090, "localhost");
    }
}
