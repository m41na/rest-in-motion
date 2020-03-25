package works.hop.handler.impl;

import works.hop.handler.HandlerIntercept;

public abstract class AbstractHandlerIntercept implements HandlerIntercept {

    protected HandlerIntercept next;

    @Override
    public void next(HandlerIntercept next) {
        this.next = next;
    }

    @Override
    public HandlerIntercept next() {
        return this.next;
    }
}
