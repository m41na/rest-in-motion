package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

public class FailureHandlerFunction implements HandlerFunction {
    @Override
    public void handle(AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise) {
        System.out.println("not looking good");
        throw new HandlerException(500, "handler handler() - major issues");
    }
}
