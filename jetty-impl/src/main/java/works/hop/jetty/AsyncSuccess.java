package works.hop.jetty;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.handler.HandlerResult;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.function.Function;

public class AsyncSuccess implements Function<HandlerResult, HandlerResult> {

    public static final Logger LOG = LoggerFactory.getLogger(AsyncSuccess.class);

    private final AsyncContext async;
    private final String target;
    private final JettyRequest request;
    private final JettyResponse response;

    public AsyncSuccess(AsyncContext async, String target, JettyRequest request, JettyResponse response) {
        this.async = async;
        this.target = target;
        this.request = request;
        this.response = response;
    }

    @Override
    public HandlerResult apply(HandlerResult res) {
        try {
            LOG.info("Servlet {} request resolved with success status. Now preparing response", target);
            if (response.forward) {
                try {
                    request.getRequestDispatcher(response.routeUri).forward(request, response);
                } catch (IOException | ServletException e) {
                    LOG.error("Exception occurred while executing 'handle()' function", e);
                    response.sendError(500, e.getMessage());
                }
            }

            if (response.redirect) {
                response.sendRedirect(response.routeUri);
            }

            if (request.error) {
                response.sendError(HttpStatus.BAD_REQUEST_400, request.message());
            }

            if (!response.isCommitted()) {
                //prepare response
                ByteBuffer content = ByteBuffer.wrap(response.getContent());
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
}
