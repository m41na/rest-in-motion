package works.hop.reducer.state;

import io.reactivex.Observer;

import java.util.Map;
import java.util.function.Consumer;

public class CombineReducers {

    private final Store store;

    public CombineReducers(Store store, Observer<State> observer, Map<String, Reducer> reducers) {
        this.store = store;
        reducers.entrySet().forEach(entry -> {
            store.reducer(entry.getKey(), entry.getValue());
            store.subscribe(entry.getKey(), observer);
        });
    }

    public void dispatcher(Consumer<Store> dispatch) {
        dispatch.accept(store);
    }
}