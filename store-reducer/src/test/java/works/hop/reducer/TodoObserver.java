package works.hop.reducer;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import works.hop.reducer.state.State;

public class TodoObserver implements Observer<State> {

    @Override
    public void onSubscribe(Disposable disposable) {
        System.out.println("subscribed to changes");
    }

    @Override
    public void onNext(State state) {
        System.out.println("observer: " + state.get());
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