package works.hop.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import works.hop.core.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;

public class JettyHandler extends AbstractHandler implements Handler<AResponseEntity> {

    private final Handler handler;

    public JettyHandler(Handler handler){
        this.handler = handler;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        CompletableFuture<AResponseEntity> entity = handle(new JettyExchange.JettyARequest(request), new JettyExchange.JettyAResponse(response));
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
        });
    }

    @Override
    public CompletableFuture<AResponseEntity> handle(ARequest ARequest, AResponse AResponse) {
        return handler.handle(ARequest, AResponse);
    }
}