package works.hop.reducer.persist;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.eclipse.jetty.servlets.EventSource;
import works.hop.jetty.sse.EventsEmitter;
import works.hop.reducer.state.Action;

import java.io.IOException;
import java.util.Map;

public class JdbcObserver implements Observer<Action>, EventsEmitter {

    private ObjectMapper mapper;
    private EventSource.Emitter emitter;

    @Override
    public void onSubscribe(Disposable disposable) {
        System.out.println("subscribed to jdbc changes");
    }

    @Override
    public void onNext(Action action) {
        if (emitter != null) {
            try {
                emitter.data(mapper.writeValueAsString(Map.of(action.getType().get(), action.getBody())));
            } catch (IOException e) {
                onError(e);
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace(System.err);
    }

    @Override
    public void onComplete() {
        System.out.println("All done");
    }

    @Override
    public void onOpen(ObjectMapper mapper, EventSource.Emitter emitter) {
        this.mapper = mapper;
        this.emitter = emitter;
    }
}