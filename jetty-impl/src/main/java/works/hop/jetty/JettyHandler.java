package works.hop.jetty;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.handler.HandlerException;
import works.hop.handler.HandlerPromise;
import works.hop.handler.HandlerResult;
import works.hop.route.Routing;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JettyHandler extends AbstractHandler {

    public static final Logger LOG = LoggerFactory.getLogger(JettyHandler.class);
    private final JettyRouter router;

    public JettyHandler(JettyRouter router) {
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
                Routing.Search route = router.search(target, baseRequest);
                if (route.result != null) {
                    LOG.info("matched route -> {}", route.result.toString());
                    aRequest.route(route);
                    HandlerPromise promise = new HandlerPromise();
                    promise.OnSuccess(new Function<HandlerResult, HandlerResult>() {
                        @Override
                        public HandlerResult apply(HandlerResult res) {
                            try {
                                LOG.info("Servlet {} request resolved with success status. Now preparing response", target);
                                if (aResponse.forward) {
                                    try {
                                        request.getRequestDispatcher(aResponse.routeUri).forward(request, response);
                                    } catch (IOException | ServletException e) {
                                        LOG.error("Exception occurred while executing 'handle()' function", e);
                                        response.sendError(500, e.getMessage());
                                    }
                                }

                                if (aResponse.redirect) {
                                    response.sendRedirect(aResponse.routeUri);
                                }

                                if (aRequest.error) {
                                    response.sendError(HttpStatus.BAD_REQUEST_400, aRequest.message());
                                }

                                if (!response.isCommitted()) {
                                    //prepare response
                                    ByteBuffer content = ByteBuffer.wrap(aResponse.getContent());
                                    //write response
                                    try (WritableByteChannel out = Channels.newChannel(response.getOutputStream())) {
                                        out.write(content);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace(System.err);
                            } finally {
                                LOG.info("Async request '{}' COMPLETED successfully: {}", target, res);
                                async.complete();
                                LOG.info("Duration of processed request -> {} ms", res.duration());
                                return res;
                            }
                        }
                    });

                    promise.OnFailure(new BiFunction<HandlerResult, Throwable, HandlerResult>() {
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
                    });

                    //handle request
                    try {
                        LOG.debug("Delegating request to handler function with completion promise");
                        route.result.handler.handle(aRequest, aResponse, promise);
                    } catch (Exception e) {
                        LOG.warn("Uncaught Exception in the promise resolver. Completing promise with failure: {}", e.getMessage());
                        e.printStackTrace(System.err);
                        promise.resolve(CompletableFuture.failedFuture(e));
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
                } catch (IOException ioe) {
                    ioe.printStackTrace(System.err);
                } finally {
                    LOG.info("Async request '{}' COMPLETED with an exception", target);
                    async.complete();
                    baseRequest.setHandled(true);
                }
            }
        });
    }
}