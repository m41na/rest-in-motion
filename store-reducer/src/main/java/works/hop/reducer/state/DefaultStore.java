package works.hop.reducer.state;

import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DefaultStore implements Store {

    private final ObservableState observableFactory = new ObservableState() {
    };
    private Map<String, Reducer> reducers = new ConcurrentHashMap<>();

    @Override
    public void reducer(String key, Reducer reducer) {
        reducer.observable(observableFactory.createObservable(reducer));
        this.reducers.putIfAbsent(key, reducer);
    }

    @Override
    public Iterable<State> state() {
        return () -> reducers.values().stream().map(red -> red.state()).iterator();
    }

    @Override
    public void dispatch(Action action) {
        reducers.entrySet().stream().forEach((entry) -> {
            String key = entry.getKey();
            reducers.get(key).apply(action);
        });
    }

    @Override
    public CompletableFuture<Boolean> dispatch(Action action, Consumer<State> consumer) {
        reducers.entrySet().stream().forEach((entry) -> {
            String key = entry.getKey();
            State newState = reducers.get(key).apply(action);
            consumer.accept(newState);
        });
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void dispatch(CompletableFuture<Action> future) {
        future.thenAccept(action -> dispatch(action));
    }

    @Override
    public void subscribe(String key, Observer<State> observer) {
        this.reducers.get(key).observable().subscribe(observer);
    }

    @Override
    public void unsubscribe(String key) {
        this.reducers.get(key).observable().unsubscribeOn(Schedulers.single());
    }
}