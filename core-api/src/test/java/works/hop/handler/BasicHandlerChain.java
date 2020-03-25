package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

import java.util.concurrent.CompletableFuture;

public class BasicHandlerChain implements HandlerChain {

    private HandlerIntercept root;

    @Override
    public HandlerIntercept root() {
        return this.root;
    }

    public void addFirst(HandlerIntercept intercept) {
        intercept.next(root);
        this.root = intercept;
    }

    @Override
    public void addLast(HandlerIntercept intercept) {
        if (root == null) {
            root = intercept;
        } else if (cycleDetected(intercept)) {
            throw new HandlerException(500, "This interceptor already exists and would form a cycle");
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
            if (head != intercept) {
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
            throw new HandlerException(500, "There is no interceptors available.");
        }
    }
}
