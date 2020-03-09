package works.hop.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import works.hop.core.RestServer;
import works.hop.core.Restful;
import works.hop.jetty.websocket.JettyWsAdapter;
import works.hop.jetty.websocket.JettyWsPolicy;
import works.hop.jetty.websocket.JettyWsProvider;
import works.hop.jetty.websocket.JettyWsServlet;
import works.hop.route.MethodRouter;
import works.hop.route.Routing;
import works.hop.traverse.Visitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public class JettyServer extends RestServer {

    private final Server server;
    private final ServerConnector connector;
    private final ContextHandlerCollection contexts = new ContextHandlerCollection();
    private final ServletContextHandler servlets = new ServletContextHandler(ServletContextHandler.SESSIONS);
    private final JettyRouter router = new JettyRouter(new MethodRouter());

    private JettyServer(Function<String, String> properties) {
        super(properties);
        //create server
        this.server = new Server();
        //add connector
        this.connector = new ServerConnector(server);
        //add routing handler
        this.addHandler(properties.apply(APP_CTX_KEY));
        //set contexts handler
        this.server.setHandler(contexts);
        // configure context for servlets
        String appctx = this.properties.apply("appctx");
        servlets.setContextPath(appctx.endsWith("/*") ? appctx.substring(0, appctx.length() - 2) : appctx.endsWith("/") ? appctx.substring(0, appctx.length() - 1) : appctx);
    }

    public static JettyServer createServer(Map<String, String> props) throws Exception {
        Properties locals = new Properties();
        props.forEach((key, value) -> locals.setProperty(key, value));
        Function<String, String> properties = key -> locals.getProperty(key);

        JettyServer server = new JettyServer(properties);
        return server;
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String host = "localhost";
        Map<String, String> props = new HashMap<>();
        props.put(APP_CTX_KEY, "/");
        JettyServer server = createServer(props);
        server.get("/", "*", "", emptyMap(), (request, response, promise) -> {
            response.ok(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date()));
            promise.complete();
        });
        server.websocket("/events/*", () -> new JettyWsAdapter());
        server.listen(port, host);
    }

    private void addHandler(String path) {
        JettyHandler handle = new JettyHandler(router);
        ContextHandler context = new ContextHandler(path);
        context.setHandler(handle);
        contexts.addHandler(context);
    }

    public Restful websocket(String ctx, JettyWsProvider provider) {
        return websocket(ctx, (JettyWsProvider) provider, JettyWsPolicy::defaultConfig);
    }

    public Restful websocket(String ctx, JettyWsProvider provider, JettyWsPolicy policy) {
        // Add a websocket destination using a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", new JettyWsServlet(provider, policy.getPolicy()));
        servlets.addServlet(holderEvents, ctx);
        return this;
    }

    @Override
    public Restful assets(String mapping, String folder) {
        return null;
    }

    @Override
    public Restful cors(Map<String, String> cors) {
        return null;
    }

    @Override
    public Restful wordpress(String home, String proxyTo) {
        return null;
    }

    @Override
    public Routing.Router getRouter() {
        return router;
    }

    @Override
    public void traverse(Visitor<Routing.Router, Routing.Route> visitor) {
        //TODO: implement traversal of tree nodes
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
    public void listen(Integer port, String host) {
        listen(port, host, null);
    }

    @Override
    public void listen(Integer port, String host, Consumer<String> result) {
        this.connector.setHost(host);
        this.connector.setPort(port);
        this.connector.setIdleTimeout(300000);

        try {
            //set the connector
            server.addConnector(connector);
            this.start();

            if (result != null) {
                result.accept(String.format("AppServer is now listening on http://%s:%d. (Press CTRL+C to quit)", host, port));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
