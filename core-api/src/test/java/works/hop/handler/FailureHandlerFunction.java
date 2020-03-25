package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;
import works.hop.handler.impl.AbstractHandlerIntercept;

public class FailureHandlerFunction extends AbstractHandlerIntercept {
  
    @Override
    public void handle(AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise) {
        System.out.println(this.getClass().getSimpleName() + " - not looking good");
        throw new HandlerException(500, "handler handler() - major issues");
    }
}
