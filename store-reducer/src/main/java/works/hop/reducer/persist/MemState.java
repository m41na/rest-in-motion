package works.hop.reducer.persist;

import works.hop.reducer.state.State;

public class MemState<S> implements State<S> {

    private S state;

    @Override
    public S apply(String recordId) {
        return state;
    }

    @Override
    public void accept(String recordId, S state) {
        this.state = state;
    }
}
