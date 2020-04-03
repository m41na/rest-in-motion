package works.hop.reducer.state;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

import java.util.function.Function;

public interface Reducer<S> extends Function<Action, State<S>> {

    String key();

    State<S> state();

    void nextState(State<S> state);

    State<S> reduce(State<S> state, Action action);

    Observable<Action> observable();

    void observable(Observable<Action> observable);

    ObservableEmitter<Action> publisher();

    void publisher(ObservableEmitter<Action> emitter);

    @Override
    default State<S> apply(Action action) {
        synchronized (this) {
            State<S> before = state();
            State<S> after = reduce(before, action);
            nextState(after);
            publisher().onNext(action);
            return after;
        }
    }
}
