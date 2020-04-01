package works.hop.reducer.state;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.reducer.Todo;
import works.hop.reducer.TodoObserver;
import works.hop.reducer.TodoReducer;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

public class CombinedReducersTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CombinedReducersTest.class);

    private ActionCreator actions = new ActionCreator();
    private Function<Action<?>, Action<Action<?>>> delegate;
    private Function<Todo, Action<Todo>> addTodo;

    @Before
    public void setUp() {
        addTodo = actions.create(() -> "ADD_TODO");
        delegate = actions.create(() -> CombinedReducers.NAME);
    }

    @Test
    public void testCombinedReducesReduceFunction() {
        String TODO_LIST = "TODO_LIST";
        Store store = new DefaultStore();
        store.reducer(CombinedReducers.NAME, new CombinedReducers<>(Map.of(TODO_LIST, new TodoReducer(TODO_LIST, new ArrayList<>()))));
        store.subscribe(CombinedReducers.NAME, new TodoObserver());
        store.state().forEach(state -> LOGGER.info("updated state - {}", state.get()));
        //dispatch event to invoke reduce method
        store.dispatch(delegate.apply(
                addTodo.apply(Todo.builder().task("yes").id(1L).completed(false).build())), state ->
                System.out.println("combiner completed"));
    }
}