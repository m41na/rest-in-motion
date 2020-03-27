package works.hop.handler.impl;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;
import works.hop.handler.*;

public class DefaultHandlerChain implements HandlerChain {

    private HandlerIntercept root;

    @Override
    public void reset() {
        this.root = null;
    }

    @Override
    public HandlerIntercept root() {
        return this.root;
    }

    @Override
    public void addFirst(HandlerFunction handler) {
        HandlerIntercept intercept = new DefaultHandlerIntercept(handler);
        intercept.next(root);
        this.root = intercept;
    }

    @Override
    public void addLast(HandlerFunction handler) {
        HandlerIntercept intercept = new DefaultHandlerIntercept(handler);
        if (root == null) {
            root = intercept;
        } else if (cycleDetected(intercept)) {
            throw new HandlerException(500, "This interceptor already exists");
        } else {
            HandlerIntercept head = root;
            while (head.next() != null) {
                head = head.next();
            }
            head.next(intercept);
        }
    }

    private boolean cycleDetected(HandlerIntercept intercept) {
        HandlerIntercept head = root;
        while (head != null) {
            if (head.handler() != intercept.handler()) {
                head = head.next();
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void intercept(HandlerFunction handler, AuthInfo auth, ARequest request, AResponse response, HandlerPromise promise) {
        if (root != null) {
            root.intercept(handler, auth, request, response, promise);
        } else {
            handler.handle(auth, request, response, promise);
        }
    }
}
