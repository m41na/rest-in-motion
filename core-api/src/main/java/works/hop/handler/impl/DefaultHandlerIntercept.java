package works.hop.handler.impl;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.handler.HandlerFunction;
import works.hop.handler.HandlerIntercept;
import works.hop.handler.HandlerPromise;

public class DefaultHandlerIntercept implements HandlerIntercept, HandlerFunction {

    private HandlerFunction handler;
    private HandlerIntercept next;

    public DefaultHandlerIntercept() {
    }

    public DefaultHandlerIntercept(HandlerFunction handler) {
        this.handler = handler;
    }

    @Override
    public void handler(HandlerFunction handler) {
        this.handler = handler;
    }

    @Override
    public HandlerFunction handler() {
        return this.handler;
    }

    @Override
    public void next(HandlerIntercept next) {
        this.next = next;
    }

    @Override
    public HandlerIntercept next() {
        return this.next;
    }

    @Override
    public void handle(ARequest request, AResponse response, HandlerPromise promise) {
        handler.handle(request, response, promise);
    }
}
