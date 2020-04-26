package works.hop.reducer;

import works.hop.reducer.state.State;

import java.util.ArrayList;
import java.util.List;

public class TodoState implements State<List<Todo>> {

    private List<Todo> state;

    public TodoState() {
        this.state = new ArrayList<>();
    }

    @Override
    public List<Todo> apply(String recordId) {
        return this.state;
    }

    @Override
    public void accept(String recordId, List<Todo> state) {
        this.state = state;
    }
}