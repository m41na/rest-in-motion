package works.hop.reducer.state;

import io.reactivex.Observable;

public interface ObservableState {

    default Observable<State> createObservable(Reducer reducer) {
        return Observable.create(observableEmitter -> reducer.publisher(observableEmitter));
    }
}
