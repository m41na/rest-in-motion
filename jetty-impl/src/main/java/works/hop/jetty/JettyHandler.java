package works.hop.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.core.AResponseEntity;
import works.hop.route.Routing;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;

public class JettyHandler extends AbstractHandler {

    public static final Logger LOG = LoggerFactory.getLogger(JettyHandler.class);
    private final JettyRouter router;

    public JettyHandler(JettyRouter router) {
        this.router = router;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final AsyncContext async = request.startAsync();
        async.start(() -> {
            //search router
            Routing.Search route = router.search(baseRequest);
            if (route.result != null) {
                LOG.info("matched route -> {}", route.result.toString());
                CompletableFuture<AResponseEntity> entity = route.result.handler.handle(new JettyExchange.JettyARequest(request), new JettyExchange.JettyAResponse(response));
                entity.thenAccept(res -> {
                    try {
                        response.setContentType("text/html; charset=utf-8");
                        response.setStatus(HttpServletResponse.SC_OK);

                        PrintWriter out = response.getWriter();
                        out.println(res.data.toString());
                        baseRequest.setHandled(true);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                    finally {
                        async.complete();
                    }
                });
            } else {
                baseRequest.setHandled(true);
            }
        });
    }
}