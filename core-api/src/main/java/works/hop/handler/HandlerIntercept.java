package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

public interface HandlerIntercept extends HandlerFunction {

    void next(HandlerIntercept next);

    HandlerIntercept next();

    default void intercept(HandlerFunction handler, AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise) {
        HandlerIntercept next = next();
        if (this != handler) {
            handle(auth, request, response);
        } else {
            handler.handle(auth, request, response, promise);
        }
        if (next != null) {
            next.intercept(handler, auth, request, response, promise);
        }
    }
}
