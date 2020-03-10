package works.hop.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import works.hop.core.Restful;
import works.hop.core.RestfulImpl;
import works.hop.jetty.startup.AppAssetsHandlers;
import works.hop.jetty.websocket.JettyWsPolicy;
import works.hop.jetty.websocket.JettyWsProvider;
import works.hop.jetty.websocket.JettyWsServlet;
import works.hop.route.Routing;
import works.hop.traverse.Visitor;

import java.util.*;
import java.util.function.Function;

public class JettyRestful extends RestfulImpl {

    protected final List<Handler> handlers = new LinkedList<>();
    protected final ServletContextHandler servlets;
    protected final Properties locals;
    protected final Map<String, String> fcgiContext = new HashMap<>();
    protected final Map<String, String> corsContext = new HashMap<>();
    private final JettyRouter router;
    private final AppAssetsHandlers assetsHandlers = new AppAssetsHandlers();

    public JettyRestful(Function<String, String> properties, JettyRouter router, Properties locals) {
        super(properties);
        this.router = router;
        this.locals = locals;
        this.servlets = new ServletContextHandler(ServletContextHandler.SESSIONS);
        this.servlets.setContextPath("/app"); //TODO: figure out how to generically configure this context
    }

    protected ContextHandler createRoutesHandler(String path) {
        JettyHandler handler = new JettyHandler(router);
        ContextHandler context = new ContextHandler();
        context.setContextPath(path);
        context.setHandler(handler);
        return context;
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
    public Restful assets(String folder) {
        handlers.add(assetsHandlers.createResourceHandler(this.properties, folder));
        return this;
    }

    @Override
    public Restful assets(String mapping, String folder) {
        String pathSpec = mapping.endsWith("/*") ? mapping.substring(0, mapping.length() - 1) : (mapping.endsWith("/") ? mapping : mapping.concat("/"));
        ServletHolder defaultServlet = assetsHandlers.createResourceServlet(this.properties, folder);
        servlets.setContextPath(pathSpec);
        servlets.addServlet(defaultServlet, pathSpec);
        return this;
    }

    @Override
    public Restful cors(Map<String, String> cors) {
        this.corsContext.putAll(cors);
        return this;
    }

    @Override
    public Restful fcgi(String home, String proxyTo) {
        this.fcgiContext.put("activate", "true");
        this.fcgiContext.put("resourceBase", home);
        this.fcgiContext.put("welcomeFile", "index.php");
        this.fcgiContext.put("proxyTo", proxyTo);
        this.fcgiContext.put("scriptRoot", home);
        return this;
    }

    @Override
    public Routing.Router getRouter() {
        return router;
    }

    @Override
    public void traverse(Visitor<Routing.Router, Routing.Route> visitor) {
        //TODO: implement traversal of tree nodes
    }
}
