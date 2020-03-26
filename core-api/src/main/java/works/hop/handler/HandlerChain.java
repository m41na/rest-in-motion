package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

public interface HandlerChain {

    void reset();

    HandlerIntercept root();

    void reset();

    void addLast(HandlerFunction intercept);

    void addFirst(HandlerFunction intercept);

    void intercept(HandlerFunction handler, AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise);
}
