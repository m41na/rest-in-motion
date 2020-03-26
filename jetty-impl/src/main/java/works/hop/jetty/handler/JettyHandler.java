package works.hop.jetty.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.handler.HandlerException;
import works.hop.handler.HandlerPromise;
import works.hop.jetty.*;
import works.hop.route.Routing;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class JettyHandler extends AbstractHandler {

    public static final Logger LOG = LoggerFactory.getLogger(JettyHandler.class);

    private final Routing.Router router;

    public JettyHandler(Routing.Router router) {
        this.router = router;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        final AsyncContext async = request.startAsync();
        async.start(() -> {
            LOG.debug("STARTED ASYNC OPERATION");
            //create wrapper objects for request/response
            JettyRequest aRequest = new JettyRequest(request);
            JettyResponse aResponse = new JettyResponse(response);

            //search router
            try {
                Routing.Search search = ((JettyRouter) router).search(target, aRequest);
                if (search.route != null) {
                    LOG.info("matched route -> {}", search.route.toString());
                    aRequest.route(search);
                    HandlerPromise promise = new HandlerPromise();
                    promise.OnSuccess(new AsyncSuccess(async, target, aRequest, aResponse));
                    promise.OnFailure(new AsyncFailure(async, target, aResponse));

                    //handle request
                    try {
                        LOG.debug("Delegating request to handler function with completion promise");
                        search.chain.intercept(search.route.handler, null, aRequest, aResponse, promise);
                    } catch (Exception e) {
                        LOG.warn("Uncaught Exception in the promise resolver. Completing promise with failure: {}", e.getMessage());
                        e.printStackTrace(System.err);
                        promise.resolve(() -> {
                        });
                    } finally {
                        baseRequest.setHandled(true);
                    }
                } else {
                    LOG.warn("no matching route handler found for request -> " + target + ". Exiting handler and setting handled to true");
                    LOG.info("Async request '{}' COMPLETED with an exception", target);
                    async.complete();
                    baseRequest.setHandled(true);
                }
            } catch (HandlerException e) {
                try {
                    //construct response manually
                    String responseString = e.getMessage();
                    response.setContentType("text/html");
                    response.setContentLength(responseString.length());
                    PrintWriter out = response.getWriter();
                    out.write(responseString);
                } catch (Exception ioe) {
                    ioe.printStackTrace(System.err);
                } finally {
                    LOG.info("Async request '{}' COMPLETED with an exception", target);
                    async.complete();
                    baseRequest.setHandled(true);
                }
            }
        });
        LOG.info("ASYNC MODE Request handling started in async context");
    }
}