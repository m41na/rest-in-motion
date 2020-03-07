package works.hop.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import works.hop.core.AResponseEntity;
import works.hop.core.Handler;
import works.hop.core.ObjectMapperSupplier;
import works.hop.core.ServerApi;
import works.hop.route.MethodRouter;
import works.hop.route.Routing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.emptyMap;

public class JettyServer extends ServerApi {

    private final Server server;
    private final ServerConnector connector;
    private final ObjectMapper mapper;
    private final ContextHandlerCollection contexts = new ContextHandlerCollection();
    private final JettyRouter router = new JettyRouter(new MethodRouter());

    private JettyServer(String base) {
        this.mapper = ObjectMapperSupplier.version1.get();
        //create server
        this.server = new Server();
        //add connector
        this.connector = new ServerConnector(server);
        //add routing handler
        this.addHandler(base);
        //set contexts handler
        this.server.setHandler(contexts);
    }

    public static JettyServer createServer(String base) throws Exception {
        JettyServer server = new JettyServer(base);
        return server;
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String host = "localhost";
        String base = "/";
        JettyServer server = createServer(base);
        server.addRoute("get", "/", "*", "", emptyMap(), (request, response) ->
                CompletableFuture.completedFuture(AResponseEntity.ok(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date()))));
        server.listen(port, host);
    }

    public void addRoute(String method, String path, String accept, String contentType, Map<String, String> headers, Handler handler) {
        Routing.Route route = Routing.RouteBuilder.newRoute()
                .handler(handler)
                .accept(accept)
                .contentType(contentType)
                .path(path)
                .method(method)
                .headers(() -> headers)
                .build();
        router.add(route);
    }

    private void addHandler(String path) {
        JettyHandler handle = new JettyHandler(router);
        ContextHandler context = new ContextHandler(path);
        context.setHandler(handle);
        contexts.addHandler(context);
    }

    @Override
    public void start() throws Exception {
        this.server.start();
        this.server.join();
    }

    @Override
    public void shutdown() throws Exception {
        this.server.stop();
    }

    @Override
    public void listen(Integer port, String host) throws Exception {
        this.connector.setHost(host);
        this.connector.setPort(port);
        this.connector.setIdleTimeout(30000);

        //set the connector
        server.addConnector(connector);
        this.start();
    }
}
