package works.hop.reducer.persist;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import redis.clients.jedis.Jedis;
import works.hop.reducer.state.Reducer;
import works.hop.reducer.state.State;

public abstract class RedisReducer<S> implements Reducer<S> {

    private final Jedis client;
    private State<S> state;
    private Observable<State<S>> observable;
    private ObservableEmitter<State<S>> publisher;

    public RedisReducer(Jedis client, S initialState) {
        this.client = client;
        this.state = () -> initialState;
    }

    @Override
    public State<S> state() {
        return this.state;
    }

    @Override
    public void nextState(State<S> state) {
        this.state = state;
    }

    @Override
    public Observable<State<S>> observable() {
        return this.observable;
    }

    @Override
    public void observable(Observable<State<S>> observable) {
        this.observable = observable;
    }

    @Override
    public ObservableEmitter<State<S>> publisher() {
        return this.publisher;
    }

    @Override
    public void publisher(ObservableEmitter<State<S>> emitter) {
        this.publisher = emitter;
    }
}