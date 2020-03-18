package works.hop.handler;

import java.util.function.Function;

public class OnSuccessPromise implements Function<HandlerResult, HandlerResult> {

    @Override
    public HandlerResult apply(HandlerResult handlerResult) {
        System.out.println("success -> duration = " + handlerResult.duration());
        return handlerResult;
    }
}
