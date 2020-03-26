package works.hop.jetty.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.handler.HandlerPromise;
import works.hop.jetty.*;
import works.hop.route.Routing;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JettyServlet extends HttpServlet {

    private final Logger LOG = LoggerFactory.getLogger(JettyServlet.class);

    private final Routing.Router router;

    public JettyServlet(Routing.Router router) {
        this.router = router;
    }

    public void handle(JettyRequest request, JettyResponse response, HandlerPromise promise) throws Exception {
        //override in subclasses to handle request/response
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //delegate dest super implementation
        super.doTrace(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //delegate dest super implementation
        super.doOptions(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //delegate dest super implementation
        super.doHead(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(new JettyRequest(req), new JettyResponse(resp));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(new JettyRequest(req), new JettyResponse(resp));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(new JettyRequest(req), new JettyResponse(resp));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(new JettyRequest(req), new JettyResponse(resp));
    }

    protected void doProcess(JettyRequest request, JettyResponse response) {
        if (request.isAsyncSupported()) {
            final AsyncContext async = request.startAsync();
            String target = request.getRequestURI();
            async.start(() -> {
                LOG.debug("STARTED ASYNC OPERATION");
                //create wrapper objects for request/response
                JettyRequest aRequest = new JettyRequest(request);
                JettyResponse aResponse = new JettyResponse(response);

                //search route
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
                            //baseRequest.setHandled(true);
                        }
                    } else {
                        LOG.warn("no matching route handler found for request -> " + target + ". Exiting handler and setting handled to true");
                        LOG.info("Async request '{}' COMPLETED with an exception", target);
                        async.complete();
                        //baseRequest.setHandled(true);
                    }
                } catch (Exception e) {
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
                        //baseRequest.setHandled(true);
                    }
                }
            });
            LOG.info("ASYNC MODE Request handling started in async context");
        } else {
            LOG.warn("*****ASYNC NOT SUPPORTED. You might need to handle writing the response in your servlet handler*****");
            HandlerPromise promise = new HandlerPromise();
            try {
                handle(request, response, promise);
            } catch (Exception e) {
                LOG.warn("Uncaught Exception in the promise resolver. Completing promise with failure: {}", e.getMessage());
                e.printStackTrace(System.err);
                promise.resolve(() -> {
                });
            }
        }
    }
}
