package works.hop.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class HandlerPromise<R> {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerPromise.class);
    private final HandlerResult result = new HandlerResult();
    private Function<HandlerResult, HandlerResult> success;
    private BiFunction<HandlerResult, Throwable, HandlerResult> failure;

    public HandlerResult resolve(Supplier<R> action) {
        LOG.info("Now will resolve promise");
        return CompletableFuture.supplyAsync(action).handle((res, th) -> {
            if (th != null) {
                if (th.getCause() != null) {
                    if (HandlerException.class.isAssignableFrom(th.getCause().getClass())) {
                        return failure.apply(result.failed(), th.getCause());
                    } else {
                        return failure.apply(result.failed(), new HandlerException(500, "promise resolver exception: " + th.getCause().getMessage(), th.getCause()));
                    }
                } else {
                    return failure.apply(result.failed(), new HandlerException(500, "promise resolver exception: " + th.getMessage(), th));
                }
            } else {
                return success.apply(result.succeeded());
            }
        }).join();
    }

    public HandlerResult complete() {
        LOG.info("Now will complete promise");
        return success.apply(result.succeeded());
    }

    public void OnSuccess(Function<HandlerResult, HandlerResult> completer) {
        this.success = completer;
    }

    public void OnFailure(BiFunction<HandlerResult, Throwable, HandlerResult> completer) {
        this.failure = completer;
    }
}
