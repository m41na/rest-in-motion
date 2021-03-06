package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;

public interface HandlerFunction {

    void handle(ARequest request, AResponse response, HandlerPromise promise);

    default void handle(ARequest request, AResponse response) {
        handle(request, response, null);
    }
}
