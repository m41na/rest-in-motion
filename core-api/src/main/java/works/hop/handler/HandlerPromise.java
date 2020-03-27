package works.hop.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HandlerPromise {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerPromise.class);
    private final HandlerResult result = new HandlerResult();
    private Function<HandlerResult, HandlerResult> success;
    private BiFunction<HandlerResult, Throwable, HandlerResult> failure;

    public HandlerResult resolve(Runnable action) {
        LOG.info("Now will resolve promise");
        return CompletableFuture.runAsync(action).handle((res, th) -> {
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

    public HandlerResult failed(String message) {
        LOG.info("Now will complete promise with failure");
        return failure.apply(result.failed(), new HandlerException(500, message));
    }

    public HandlerResult complete() {
        LOG.info("Now will complete promise successfully");
        return success.apply(result.succeeded());
    }

    public Boolean canProceed() {
        return !this.result.completed();
    }

    public void OnSuccess(Function<HandlerResult, HandlerResult> completer) {
        this.success = completer;
    }

    public void OnFailure(BiFunction<HandlerResult, Throwable, HandlerResult> failure) {
        this.failure = failure;
    }
}
