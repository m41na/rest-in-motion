package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

/**
 * Usage notes:
 * In the 'before' handlers,
 * 1. always call next() to signal successful handling
 * 2. prefer calling failure() or optionally throw an exception to signal failed handling
 * In the 'after' handler, the chain will ignore calls to done() or failure() since the response is already completed
 * - use these to do work unrelated to request/response processing, like cleanup/tear-down
 * In the request handler, use 'resolve(runnable) or complete() method to signal that the response is ready for sending
 * - failure to do so will result in the thread blocking, and eventually timing out
 */
public interface HandlerIntercept {

    void handler(HandlerFunction handler);

    HandlerFunction handler();

    void next(HandlerIntercept next);

    HandlerIntercept next();

    default void intercept(HandlerFunction handler, AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise) {
        //intercept before handler
        handler().handle(request, response, promise);
        if (promise.canProceed()) {
            //check next handler
            HandlerIntercept next = next();
            if (next != null) {
                if (next.handler() == handler) {
                    next.handler().handle(request, response, promise);
                    next.interceptAfter(handler, auth, request, response, promise);
                } else {
                    next.intercept(handler, auth, request, response, promise);
                }
            }
        }
    }

    default void interceptAfter(HandlerFunction handler, AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise) {
        //intercept after handler
        HandlerIntercept next = next();
        if (next != null) {
            next.handler().handle(request, response, promise);
            next.interceptAfter(handler, auth, request, response, promise);
        }
    }
}
