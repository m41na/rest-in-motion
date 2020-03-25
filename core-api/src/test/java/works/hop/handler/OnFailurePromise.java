package works.hop.handler;

import java.util.function.BiFunction;

public class OnFailurePromise implements BiFunction<HandlerResult, Throwable, HandlerResult> {

    public HandlerResult result;
    public Throwable failure;

    @Override
    public HandlerResult apply(HandlerResult handlerResult, Throwable throwable) {
        this.result = handlerResult;
        this.failure = throwable;
        System.out.println(this.getClass().getSimpleName() + " - failure -> " + throwable.getMessage());
        return handlerResult;
    }
}
