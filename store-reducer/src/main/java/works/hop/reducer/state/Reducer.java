package works.hop.reducer.state;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

import java.util.function.Function;

public interface Reducer<S> extends Function<Action, State<S>> {

    String key();

    State<S> state();

    void nextState(State<S> state);

    State<S> reduce(State<S> state, Action action);

    Observable<State<S>> observable();

    void observable(Observable<State<S>> observable);

    ObservableEmitter<State<S>> publisher();

    void publisher(ObservableEmitter<State<S>> emitter);

    @Override
    default State<S> apply(Action action) {
        synchronized (this) {
            State<S> before = state();
            State<S> after = reduce(before, action);
            nextState(after);
            publisher().onNext(after);
            return after;
        }
    }
}
