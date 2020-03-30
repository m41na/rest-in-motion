package works.hop.reducer;

import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import works.hop.core.JsonResult;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.ActionCreator;
import works.hop.reducer.state.DefaultStore;
import works.hop.reducer.state.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static works.hop.jetty.JettyStartable.createServer;
import static works.hop.jetty.startup.AppOptions.applyDefaults;

public class TodoWebApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoWebApp.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(TodoConfig.class);
        TodoService repo = ctx.getBean("todo-repo", TodoService.class);

        final String TODO_LIST = "TODO_LIST";
        ActionCreator actions = new ActionCreator();
        Function<Void, Action<Todo>> allTodo = actions.create(() -> "ALL_TODO");
        Function<Todo, Action<Todo>> addTodo = actions.create(() -> "ADD_TODO");
        Function<String, Action<Todo>> removeTodo = actions.create(() -> "REMOVE_TODO");
        Function<String, Action<Todo>> completeTodo = actions.create(() -> "COMPLETE_TODO");
        Store store = new DefaultStore();
        store.reducer(TODO_LIST, new TodoReducer(new ArrayList<>()));
        store.subscribe(TODO_LIST, new TodoObserver());
        store.state().forEach(state -> LOGGER.info("updated state - {}", state.get()));

        Map<String, String> properties = applyDefaults(new Options(), args);
        var app = createServer(properties.get("appctx"), properties);
        app.before((req, res, done) -> {
            List<Todo> todos = repo.fetchAll();
            todos.stream().forEach(todo -> store.dispatch(addTodo.apply(todo)));
            System.out.println("PRINT BEFORE ALL /");
        });
        app.before("get", "/", (req, res, done) -> {
            System.out.println("PRINT BEFORE GET /");
        });
        app.get("/", (req, res, done) -> done.resolve(() -> {
            var future = store.dispatch(allTodo.apply(null), result -> res.json(JsonResult.ok(result.get())));
            future.join();
        }));
        app.post("/{name}", (req, res, done) -> done.resolve(() -> {
            String task = req.param("name");
            Todo todo = repo.save(task);
            store.dispatch(addTodo.apply(todo));
            res.json(JsonResult.ok(todo));
        }));
        app.put("/{name}", (req, res, done) -> done.resolve(() -> {
            String task = req.param("name");
            int updated = repo.update(task);
            assert updated == 1;
            store.dispatch(completeTodo.apply(task));
            var future = store.dispatch(allTodo.apply(null), result -> res.json(JsonResult.ok(result.get())));
            future.join();
        }));
        app.delete("/{name}", (req, res, done) -> done.resolve(() -> {
            String task = req.param("name");
            int deleted = repo.remove(task);
            assert deleted == 1;
            store.dispatch(removeTodo.apply(task));
            var future = store.dispatch(allTodo.apply(null), result -> res.json(JsonResult.ok(result.get())));
            future.join();
        }));
        //app.after("get", "/", (auth, req, res, done) -> System.out.println("PRINT AFTER GET /"));
        app.after((req, res, done) -> System.out.println("PRINT AFTER GET /"));
        app.listen(8090, "localhost");
    }
}
