package works.hop.reducer.state;

import works.hop.reducer.persist.MemState;

import java.util.function.Function;

public interface State<S> extends Function<String, S> {

    static <S> State<S> of(S initialState) {
        return new MemState<>();
    }

    void accept(String recordId, S state);
}
