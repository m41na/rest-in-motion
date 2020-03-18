package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

import java.util.concurrent.CompletableFuture;

public interface HandlerIntercept {

    void next(HandlerIntercept next);

    HandlerIntercept next();

    CompletableFuture<HandlerResult> before(AuthInfo auth, ARequest request, AResponse response);

    CompletableFuture<HandlerResult> after(AuthInfo auth, ARequest request, AResponse response, HandlerResult result);

    CompletableFuture<HandlerResult> otherwise(AuthInfo auth, ARequest request, AResponse response, HandlerResult result);

    default CompletableFuture<HandlerResult> intercept(HandlerFunction handler, AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise) {
        return before(auth, request, response).thenCompose(ok -> {
            if (ok.success()) {
                if (next() != null) {
                    return next().intercept(handler, auth, request, response, promise);
                } else {
                    handler.handle(auth, request, response, promise);
                    return CompletableFuture.completedFuture(ok);
                }
            } else {
                return otherwise(auth, request, response, ok);
            }
        }).thenCompose(ok -> after(auth, request, response, ok)).handle((res, th) -> {
            if (th != null) {
                System.out.println(th.toString());
                throw new HandlerException(500, th.getMessage());
            }
            return res;
        });
    }
}
