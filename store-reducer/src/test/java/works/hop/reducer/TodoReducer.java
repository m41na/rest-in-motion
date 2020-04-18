package works.hop.reducer;

import works.hop.reducer.persist.RecordKey;
import works.hop.reducer.state.AbstractReducer;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.State;

import java.util.List;
import java.util.stream.Collectors;

public class TodoReducer extends AbstractReducer<List<Todo>> {

    public TodoReducer(String name, State<List<Todo>> initialState) {
        super(name, initialState);
    }

    @Override
    public List<Todo> reduce(State<List<Todo>> state, Action action) {
        switch (action.getType().get()) {
            case "ADD_TODO":
                List<Todo> added = state.apply(null, null);
                added.add((Todo) action.getBody());
                return added;
            case "REMOVE_TODO":
                String toRemove = (String) action.getBody();
                return state.apply(null, null).stream().filter(todo1 -> !todo1.task.equalsIgnoreCase(toRemove)).collect(Collectors.toList());
            case "COMPLETE_TODO":
                String toComplete = (String) action.getBody();
                return state.apply(null, null).stream().map(todo -> {
                    if (todo.task.equalsIgnoreCase(toComplete)) {
                        return Todo.builder().id(todo.id).task(todo.task).completed(!todo.completed).build();
                    } else {
                        return todo;
                    }
                }).collect(Collectors.toList());
            default:
                return state.apply(null, null);
        }
    }

    @Override
    public Object compute(Action action) {
        switch (action.getType().get()) {
            case "ALL_TODO":
                RecordKey toFetch = (RecordKey) action.getBody();
                return this.state().apply(toFetch.getUserKey(), toFetch.getCollectionKey());
            case "TODO_BY_ID":
                long fetchId = (long) action.getBody();
                return this.state().apply(null, null).stream().filter(r -> r.id == fetchId).findFirst().get();
            default:
                return null;
        }
    }
}