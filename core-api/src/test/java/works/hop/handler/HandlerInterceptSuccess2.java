package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;
import works.hop.handler.impl.AbstractHandlerIntercept;

public class HandlerInterceptSuccess2 extends AbstractHandlerIntercept {

    @Override
    public void handle(AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise) {
        System.out.println(this.getClass().getSimpleName() + " - simulate successful interceptor");
    }
}
