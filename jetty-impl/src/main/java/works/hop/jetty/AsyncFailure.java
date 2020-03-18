package works.hop.jetty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.handler.HandlerException;
import works.hop.handler.HandlerResult;

import javax.servlet.AsyncContext;
import java.util.function.BiFunction;

public class AsyncFailure implements BiFunction<HandlerResult, Throwable, HandlerResult> {

    public static final Logger LOG = LoggerFactory.getLogger(AsyncFailure.class);

    private final AsyncContext async;
    private final String target;
    private final JettyResponse response;

    public AsyncFailure(AsyncContext async, String target, JettyResponse response) {
        this.async = async;
        this.target = target;
        this.response = response;
    }

    @Override
    public HandlerResult apply(HandlerResult res, Throwable th) {
        try {
            LOG.info("Servlet {} request resolved with failure status. Now preparing response", target);
            int status = 500;
            if (HandlerException.class.isAssignableFrom(th.getClass())) {
                status = ((HandlerException) th).status;
            }
            response.sendError(status, th.getMessage());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            LOG.info("Async request '{}' COMPLETED with an exception", target);
            async.complete();
            LOG.info("Duration of processed request -> {} ms", res.duration());
            return res;
        }
    }
}
