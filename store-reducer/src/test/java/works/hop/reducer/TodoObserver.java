package works.hop.reducer;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import works.hop.reducer.state.Action;

public class TodoObserver implements Observer<Action> {

    @Override
    public void onSubscribe(Disposable disposable) {
        System.out.println("subscribed to changes");
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