package works.hop.reducer.state;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

import java.util.function.Function;

public interface Reducer<S> extends Function<Action, S> {

    String key();

    State<S> state();

    default void nextState(S state) {
        nextState(null, null, state);
    }

    void nextState(String user, String collection, S state);

    S reduce(State<S> state, Action action);

    Object compute(Action action);

    Observable<Action> observable();

    void observable(Observable<Action> observable);

    ObservableEmitter<Action> publisher();

    void publisher(ObservableEmitter<Action> emitter);

    @Override
    default S apply(Action action) {
        synchronized (this) {
            State<S> before = state();
            S after = reduce(before, action);
            nextState(after);
            publisher().onNext(action);
            return after;
        }
    }
}
