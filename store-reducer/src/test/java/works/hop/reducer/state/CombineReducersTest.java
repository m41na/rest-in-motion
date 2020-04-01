package works.hop.reducer.state;

import org.junit.Before;
import org.junit.Test;
import works.hop.reducer.Todo;
import works.hop.reducer.TodoObserver;
import works.hop.reducer.TodoReducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static junit.framework.TestCase.assertEquals;

public class CombineReducersTest {

    private ActionCreator actions = new ActionCreator();
    private Function<Todo, Action<Todo>> addTodo;

    @Before
    public void setUp() {
        addTodo = actions.create(() -> "ADD_TODO");
    }

    @Test
    public void testCombinedReducesReduceFunction() {
        String FIRST_TODO = "FIRST_TODO", SECOND_TODO = "SECOND_TODO";
        Reducer firstRed = new TodoReducer(FIRST_TODO, new ArrayList<>());
        Reducer secondRed = new TodoReducer(SECOND_TODO, new ArrayList<>());

        CombineReducers combinedReducers = new CombineReducers(new DefaultStore(), new TodoObserver(),
                Map.of(FIRST_TODO, firstRed, SECOND_TODO, secondRed));
        //dispatch event to invoke reduce method
        combinedReducers.dispatcher((store) -> store.dispatch(addTodo.apply(Todo.builder().task("yes").id(1L).completed(false).build())));
        assertEquals(1, ((List) firstRed.state().get()).size());
        assertEquals(1, ((List) secondRed.state().get()).size());
    }
}