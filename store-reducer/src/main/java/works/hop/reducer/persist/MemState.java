package works.hop.reducer.persist;

import works.hop.reducer.state.State;

public class MemState<S> implements State<S> {

    private S state;

    @Override
    public S apply(String user, String collection) {
        return state;
    }

    @Override
    public void accept(String user, String collection, S state) {
        this.state = state;
    }
}
