package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

public interface HandlerFunction {

    void handle(AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise);

    default void handle(AuthInfo auth, ARequest request, AResponse response) {
        handle(auth, request, response, null);
    }
}
