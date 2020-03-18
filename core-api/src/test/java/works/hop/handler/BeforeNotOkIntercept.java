package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

import java.util.concurrent.CompletableFuture;

public class BeforeNotOkIntercept implements HandlerIntercept {

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
    public CompletableFuture before(AuthInfo auth, ARequest request, AResponse response) {
        return CompletableFuture.failedFuture(new RuntimeException("before() - we got problems"));
    }

    @Override
    public CompletableFuture<HandlerResult> otherwise(AuthInfo auth, ARequest request, AResponse response, HandlerResult result) {
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<HandlerResult> after(AuthInfo auth, ARequest request, AResponse response, HandlerResult result) {
        return CompletableFuture.completedFuture(result);
    }
}
