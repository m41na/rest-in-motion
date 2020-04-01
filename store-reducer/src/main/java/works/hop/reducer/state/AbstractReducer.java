package works.hop.reducer.state;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public abstract class AbstractReducer<S> implements Reducer<S> {

    private String name;
    private State<S> state;
    private Observable<State<S>> observable;
    private ObservableEmitter<State<S>> publisher;

    public AbstractReducer(String name, S initialState) {
        this.name = name;
        this.state = () -> initialState;
    }

    @Override
    public String key() {
        return this.name;
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