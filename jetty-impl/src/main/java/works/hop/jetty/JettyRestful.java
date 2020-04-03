package works.hop.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;
import works.hop.core.Restful;
import works.hop.jetty.builder.ContextHandlerBuilder;
import works.hop.jetty.builder.ObjectMapperSupplier;
import works.hop.jetty.handler.JettyHandler;
import works.hop.jetty.servlet.ServletConfig;
import works.hop.jetty.session.SessionUtil;
import works.hop.jetty.sse.AppEventSource;
import works.hop.jetty.sse.EventsEmitter;
import works.hop.jetty.startup.AppAssetsHandlers;
import works.hop.jetty.startup.AppFcgiHandler;
import works.hop.jetty.websocket.JettyWsPolicy;
import works.hop.jetty.websocket.JettyWsProvider;
import works.hop.jetty.websocket.JettyWsServlet;
import works.hop.route.Routing;
import works.hop.traverse.Visitor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JettyRestful implements Restful {

    final String APP_CTX_KEY = "appctx";
    final List<Handler> rootHandlers = new LinkedList<>();
    final ContextHandlerCollection contextHandlers = new ContextHandlerCollection();
    final Function<String, String> properties;
    final JettyRouter router;

    public JettyRestful(Function<String, String> properties, JettyRouter router) {
        this.router = router;
        this.properties = properties;
    }

    @Override
    public Restful mount(String path) {
        JettyHandler routeHandler = new JettyHandler(router);
        ContextHandler context = new ContextHandler();
        context.setContextPath(path);
        context.setHandler(routeHandler);
        this.contextHandlers.addHandler(context);
        return this;
    }

    public Restful mount(String path, Function<ContextHandlerBuilder, ServletContextHandler> builder) {
        contextHandlers.addHandler(builder.apply(ContextHandlerBuilder.newBuilder(path, router, null)));
        return this;
    }

    @Override
    public Restful session(Function<String, String> properties) {
        String url = properties.apply("session.jdbc.url");
        String driver = properties.apply("session.jdbc.driver");
        SessionHandler sessionHandler = SessionUtil.sqlSessionHandler(driver, url);
        rootHandlers.add(sessionHandler);
        return this;
    }

    @Override
    public Restful assets(String folder) {
        rootHandlers.add(AppAssetsHandlers.createResourceHandler(this.properties, folder));
        return this;
    }

    @Override
    public Restful assets(String mapping, String folder) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(mapping);
        context.setHandler(AppAssetsHandlers.createResourceHandler(this.properties, folder));
        contextHandlers.addHandler(context);
        return this;
    }

    @Override
    public Function<String, String> properties() {
        return this.properties;
    }

    @Override
    public Restful fcgi(String context, String home, String proxyTo) {
        Map<String, String> fcgiContext = AppFcgiHandler.defaultFcgProperties(context, home, proxyTo);
        contextHandlers.addHandler(AppFcgiHandler.createFcgiHandler(fcgiContext));
        return this;
    }

    @Override
    public Routing.Router getRouter() {
        return router;
    }

    @Override
    public String getContext() {
        return this.properties.apply(APP_CTX_KEY);
    }

    @Override
    public void traverse(Visitor<Routing.Router> visitor) {
        visitor.visit(router);
    }
}
