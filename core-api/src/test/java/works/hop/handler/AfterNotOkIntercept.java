package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

import java.util.concurrent.CompletableFuture;

public class AfterNotOkIntercept implements HandlerIntercept {

    HandlerIntercept next;

    @Override
    public void next(HandlerIntercept next) {
        this.next = next;
    }

    @Override
    public HandlerIntercept next() {
        return this.next;
    }

    @Override
    public CompletableFuture<HandlerResult> before(AuthInfo auth, ARequest request, AResponse response) {
        return CompletableFuture.completedFuture(HandlerResult.ok());
    }

    @Override
    public CompletableFuture<HandlerResult> otherwise(AuthInfo auth, ARequest request, AResponse response, HandlerResult result) {
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<HandlerResult> after(AuthInfo auth, ARequest request, AResponse response, HandlerResult result) {
        result.failed();
        return CompletableFuture.failedFuture(new RuntimeException("after() - we got more issues"));
    }
}
