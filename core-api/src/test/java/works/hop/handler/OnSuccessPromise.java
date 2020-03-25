package works.hop.handler;

import java.util.function.Function;

public class OnSuccessPromise implements Function<HandlerResult, HandlerResult> {

    public HandlerResult result;

    @Override
    public HandlerResult apply(HandlerResult handlerResult) {
        this.result = handlerResult;
        System.out.println(this.getClass().getSimpleName() + " - success -> duration = " + handlerResult.duration());
        return handlerResult;
    }
}
