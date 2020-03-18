package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

import java.util.concurrent.CompletableFuture;

public interface HandlerChain {

    HandlerIntercept root();

    void addLast(HandlerIntercept intercept);

    CompletableFuture<HandlerResult> intercept(HandlerFunction handler, AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise);
}
