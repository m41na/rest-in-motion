package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

public class SuccessHandlerFunction implements HandlerFunction {
    @Override
    public void handle(AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise) {
        System.out.println("looking good");
        promise.resolve(() -> "all clear");
    }
}
