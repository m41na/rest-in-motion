package works.hop.handler;

import java.util.function.BiFunction;

public class OnFailurePromise implements BiFunction<HandlerResult, Throwable, HandlerResult> {
    @Override
    public HandlerResult apply(HandlerResult handlerResult, Throwable throwable) {
        System.out.println("failure -> " + throwable.getMessage());
        return handlerResult;
    }
}
