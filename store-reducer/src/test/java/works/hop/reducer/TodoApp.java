package works.hop.reducer;

import works.hop.reducer.state.Action;
import works.hop.reducer.state.ActionCreator;
import works.hop.reducer.state.DefaultStore;
import works.hop.reducer.state.Store;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class TodoApp {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String reducerKey = "todos";
        ActionCreator actions = new ActionCreator();
        Function<Void, Action<Todo>> allTodo = actions.create(() -> "ALL_TODO");
        Function<Todo, Action<Todo>> addTodo = actions.create(() -> "ADD_TODO");
        Function<String, Action<Todo>> removeTodo = actions.create(() -> "REMOVE_TODO");
        Function<String, Action<Todo>> completeTodo = actions.create(() -> "COMPLETE_TODO");
        TodoState state = new TodoState();
        Store store = new DefaultStore();
        store.reducer(reducerKey, new TodoReducer(reducerKey, state));
        store.subscribe(reducerKey, new TodoObserver());
        //dispatch some actions (with future)
        store.dispatchAsync(CompletableFuture.supplyAsync(() -> addTodo.apply(new Todo("100", "butter", false))));
        //dispatch some actions (same thread as dispatcher)
        store.dispatch(addTodo.apply(new Todo("120", "milk", false)));
        store.dispatch(addTodo.apply(new Todo("140", "bread", false)));
        store.dispatch(completeTodo.apply("milk"));
        store.dispatch(removeTodo.apply("bread"));
        store.state().forEach(records -> {
            System.out.println(records);
        });
        store.dispatchAsync(allTodo.apply(null), result -> System.out.println("todos state -> " + result)).get();
        store.unsubscribe(reducerKey);
    }
}
