package works.hop.reducer;

import works.hop.reducer.state.AbstractReducer;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.State;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TodoReducer extends AbstractReducer<List<Todo>> {

    public TodoReducer(List<Todo> initialState) {
        super(initialState);
    }

    @Override
    public State<List<Todo>> reduce(State<List<Todo>> state, Action action) {
        switch (action.getType().get()) {
            case "ADD_TODO":
                List<Todo> added = new ArrayList<>(state.get());
                added.add((Todo) action.getBody());
                return () -> added;
            case "REMOVE_TODO":
                String toRemove = (String) action.getBody();
                List<Todo> removed = state.get().stream().filter(todo1 -> !todo1.task.equalsIgnoreCase(toRemove)).collect(Collectors.toList());
                return () -> removed;
            case "COMPLETE_TODO":
                String toComplete = (String) action.getBody();
                List<Todo> updated = state.get().stream().map(todo -> {
                    if (todo.task.equalsIgnoreCase(toComplete)) {
                        return Todo.builder().id(todo.id).task(todo.task).completed(!todo.completed).build();
                    } else {
                        return todo;
                    }
                }).collect(Collectors.toList());
                return () -> updated;
            default:
                return state;
        }
    }
}