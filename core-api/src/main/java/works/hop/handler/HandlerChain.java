package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

public interface HandlerChain {

    HandlerIntercept root();

    void addLast(HandlerIntercept intercept);

    void addFirst(HandlerIntercept intercept);

    void intercept(HandlerFunction handler, AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise);
}
