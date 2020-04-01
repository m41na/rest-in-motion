package works.hop.reducer.state;

import java.util.Map;
import java.util.function.Function;

public class CombinedReducers<S> extends AbstractReducer<Map<String, AbstractReducer<?>>> {

    public static String NAME = "COMBINED_REDUCERS";
    public Function<Action<?>, Action<Action<?>>> delegate;

    public CombinedReducers(Map<String, AbstractReducer<?>> initialState) {
        super("COMBINED_REDUCERS", initialState);
        ActionCreator actions = new ActionCreator();
        delegate = actions.create(() -> NAME);
    }

    @Override
    public State<Map<String, AbstractReducer<?>>> reduce(State<Map<String, AbstractReducer<?>>> state, Action action) {
        String type = ((Action<?>) action.getBody()).getType().get();
        Reducer<?> nestedReducer = state.get().get("TODO_LIST");
        //Action<?> body = (Action<?>) ((Action<?>) action.getBody()).getBody();
        //return nestedReducer.reduce(nestedReducer.state().get(), body);
        return null;
    }
}