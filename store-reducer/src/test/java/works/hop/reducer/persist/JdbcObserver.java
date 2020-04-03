package works.hop.reducer.persist;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import works.hop.reducer.state.State;

public class JdbcObserver implements Observer<State> {

    @Override
    public void onSubscribe(Disposable disposable) {
        System.out.println("subscribed to changes");
    }

    @Override
    public void onNext(State state) {
        System.out.println("observer: " + state);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace(System.err);
    }

    @Override
    public void onComplete() {
        System.out.println("All done");
    }
}