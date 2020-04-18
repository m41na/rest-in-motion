package works.hop.reducer.state;

import io.reactivex.Observer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface Store {

    void reducer(String key, Reducer reducer);

    Iterable<State> state();

    void dispatch(Action action);

    void dispatchAsync(CompletableFuture<Action> future);

    <S> CompletableFuture<Boolean> dispatchAsync(Action action, Consumer<S> consumer);

    <S> CompletableFuture<Boolean> dispatchQuery(Action action, Consumer<S> consumer);

    void subscribe(String key, Observer<Action> observer);

    void unsubscribe(String key);
}