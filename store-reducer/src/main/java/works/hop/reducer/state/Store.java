package works.hop.reducer.state;

import io.reactivex.Observer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface Store {

    void reducer(String key, Reducer reducer);

    Iterable<State> state();

    void dispatch(Action action);

    void dispatch(CompletableFuture<Action> future);

    CompletableFuture<Boolean> dispatch(Action action, Consumer<State> state);

    void subscribe(String key, Observer<Action> observer);

    void unsubscribe(String key);
}