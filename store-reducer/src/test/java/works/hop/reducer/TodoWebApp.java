package works.hop.reducer;

import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import works.hop.core.JsonResult;
import works.hop.reducer.config.PersistTestConfig;
import works.hop.reducer.persist.JdbcReducer;
import works.hop.reducer.persist.RecordCriteria;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.ActionCreator;
import works.hop.reducer.state.DefaultStore;
import works.hop.reducer.state.Store;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static works.hop.jetty.JettyStartable.createServer;
import static works.hop.jetty.startup.AppOptions.applyDefaults;

public class TodoWebApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoWebApp.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(PersistTestConfig.class);
        DataSource dataSource = ctx.getBean(DataSource.class);

        final String TODO_LIST = "TODO_LIST";
        ActionCreator actions = new ActionCreator();
        //available actions
        Function<RecordCriteria, Action<List<Todo>>> fetchAllRecords = actions.create(() -> JdbcReducer.FETCH_ALL);
        Function<RecordEntity, Action<List<RecordEntity>>> createRecord = actions.create(() -> JdbcReducer.CREATE_RECORD);
        Function<RecordCriteria, Action<List<RecordEntity>>> deleteRecord = actions.create(() -> JdbcReducer.DELETE_RECORD);
        Function<RecordEntity, Action<List<RecordEntity>>> updateRecord = actions.create(() -> JdbcReducer.UPDATE_RECORD);
        //create reducer
        Store store = new DefaultStore();
        store.reducer(TODO_LIST, new JdbcReducer<List<Todo>>(dataSource, TODO_LIST, new ArrayList<>()));
        store.subscribe(TODO_LIST, new TodoObserver());
        store.state().forEach(state -> LOGGER.info("updated state - {}", state.get()));

        Map<String, String> properties = applyDefaults(new Options(), args);
        var app = createServer(properties.get("appctx"), properties);
        app.before((req, res, done) -> System.out.println("PRINT BEFORE ALL /"));
        app.before("get", "/", (req, res, done) -> System.out.println("PRINT BEFORE GET /"));
        app.get("/", (req, res, done) -> done.resolve(() -> {
            res.send("TODO web app using jdbc reducer");
        }));
        app.get("/{user}", (req, res, done) -> {
            String userKey = req.param("user");
            RecordCriteria criteria = RecordCriteria.builder().collectionKey(TODO_LIST).userKey(userKey).build();
            done.resolve(store.dispatch(fetchAllRecords.apply(criteria), state -> {
                res.json(JsonResult.ok(state.get()));
            }));
        });
        app.post("/{user}", "application/json", "application/json", (req, res, done) -> {
            String userKey = req.param("user");
            Todo todo = req.body(Todo.class);
            RecordEntity recordEntity = RecordEntity.builder().userKey(userKey).collectionKey(TODO_LIST).dataValue(todo).build();
            done.resolve(store.dispatch(createRecord.apply(recordEntity), state -> {
                res.json(JsonResult.ok(state.get()));
            }));
        });
        app.put("/{user}", (req, res, done) -> {
            String userKey = req.param("user");
            Todo todo = req.body(Todo.class);
            RecordEntity recordEntity = RecordEntity.builder().userKey(userKey).collectionKey(TODO_LIST).dataValue(todo).build();
            done.resolve(store.dispatch(updateRecord.apply(recordEntity), state -> {
                res.json(JsonResult.ok(state.get()));
            }));
        });
        app.delete("/{user}/{id}", (req, res, done) -> {
            String userKey = req.param("user");
            Long todoId = req.longParam("id");
            RecordCriteria deleteId = RecordCriteria.builder().id(todoId).userKey(userKey).collectionKey(TODO_LIST).build();
            done.resolve(store.dispatch(deleteRecord.apply(deleteId), state -> {
                res.json(JsonResult.ok(state.get()));
            }));
        });
        app.after("get", "/", (req, res, done) -> System.out.println("PRINT AFTER GET /"));
        app.after((req, res, done) -> System.out.println("PRINT AFTER ALL /"));
        app.listen(8090, "localhost");
    }
}
