package works.hop.reducer.persist;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import works.hop.reducer.state.Action;

public class RedisObserver implements Observer<Action> {

    @Override
    public void onSubscribe(Disposable disposable) {
        System.out.println("subscribed to redis changes");
    }

    @Override
    public void onNext(Action action) {
        System.out.println("observer: " + action);
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