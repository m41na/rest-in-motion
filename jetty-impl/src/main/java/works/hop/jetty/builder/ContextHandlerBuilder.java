package works.hop.jetty.builder;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;
import works.hop.core.Restful;
import works.hop.jetty.filter.HandlerFilter;
import works.hop.jetty.servlet.JettyServlet;
import works.hop.jetty.servlet.ServletConfig;
import works.hop.jetty.sse.AppEventSource;
import works.hop.jetty.sse.EventsEmitter;
import works.hop.jetty.startup.AppAssetsHandlers;
import works.hop.jetty.startup.AppCorsFilter;
import works.hop.jetty.websocket.JettyWsPolicy;
import works.hop.jetty.websocket.JettyWsProvider;
import works.hop.jetty.websocket.JettyWsServlet;
import works.hop.route.Routing;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;

public class ContextHandlerBuilder {

    private ServletContextHandler servletContext;
    private Routing.Router router;

    private ContextHandlerBuilder(Routing.Router router) {
        this.router = router;
        this.servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
    }

    public static ContextHandlerBuilder newBuilder(String context, Routing.Router router, ServletConfig config) {
        return new ContextHandlerBuilder(router).context(context, config);
    }

    private ContextHandlerBuilder context(String path, ServletConfig config) {
        this.servletContext.setContextPath(path);
        JettyServlet handler = new JettyServlet(router);
        ServletHolder holder = new ServletHolder(handler);
        if (config != null) config.configure(holder);
        servletContext.addServlet(holder, "/*");
        return this;
    }

    public ContextHandlerBuilder assets(String folder, Function<String, String> properties) {
        return assets("/", folder, properties);
    }

    public ContextHandlerBuilder assets(String mapping, String folder, Function<String, String> properties) {
        String pathSpec = mapping.endsWith("/*") ? mapping.substring(0, mapping.length() - 1) : (mapping.endsWith("/") ? mapping : mapping.concat("/"));
        ServletHolder defaultServlet = AppAssetsHandlers.createResourceServlet(properties, folder);
        servletContext.addServlet(defaultServlet, pathSpec);
        return this;
    }

    public ContextHandlerBuilder filter(HandlerFilter filter) {
        return filter("/*", filter);
    }

    public ContextHandlerBuilder filter(String context, HandlerFilter filter) {
        FilterHolder holder = new FilterHolder(filter);
        servletContext.addFilter(holder, context, EnumSet.of(DispatcherType.REQUEST));
        return this;
    }

    public ContextHandlerBuilder servlet(String mapping, ServletConfig config, HttpServlet handler) {
        String pathSpec = mapping.endsWith("/*") ? mapping : (mapping.endsWith("/") ? mapping.concat("*") : mapping.concat("/*"));
        ServletHolder holder = new ServletHolder(handler);
        if (config != null) config.configure(holder);
        servletContext.addServlet(holder, pathSpec);
        return this;
    }

    public ContextHandlerBuilder websocket(String path, JettyWsPolicy policy, JettyWsProvider provider) {
        String pathSpec = path.endsWith("/*") ? path : (path.endsWith("/") ? path.substring(0, path.length() - 1) : path.concat("/*"));
        ServletHolder handlerHolder = new ServletHolder("websocket-".concat(path), new JettyWsServlet(provider, policy.getPolicy()));
        servletContext.addServlet(handlerHolder, pathSpec);
        return this;
    }

    public ContextHandlerBuilder sse(String path, ServletConfig config, EventsEmitter eventsEmitter) {
        String pathSpec = path.endsWith("/*") ? path : (path.endsWith("/") ? path.substring(0, path.length() - 1) : path.concat("/*"));
        ServletHolder handlerHolder = new ServletHolder(new EventSourceServlet() {
            @Override
            protected EventSource newEventSource(HttpServletRequest request) {
                return new AppEventSource(request) {

                    @Override
                    public void onOpen(EventSource.Emitter emitter) throws IOException {
                        super.onOpen(emitter);
                        eventsEmitter.onOpen(ObjectMapperSupplier.version2.get(), emitter);
                    }
                };
            }
        });
        if (config != null) config.configure(handlerHolder);
        servletContext.addServlet(handlerHolder, pathSpec);
        return this;
    }

    public ContextHandlerBuilder cors(Map<String, String> corsContext) {
        FilterHolder corsFilter = AppCorsFilter.configCorsFilter(corsContext);
        this.servletContext.addFilter(corsFilter, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
        return this;
    }

    public ServletContextHandler build() {
        return this.servletContext;
    }
}
