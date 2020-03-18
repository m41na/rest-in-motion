package works.hop.jetty.builder;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import works.hop.jetty.servlet.JettyServlet;
import works.hop.jetty.servlet.ServletConfig;
import works.hop.jetty.startup.AppAssetsHandlers;
import works.hop.jetty.startup.AppCorsFilter;
import works.hop.jetty.websocket.JettyWsPolicy;
import works.hop.jetty.websocket.JettyWsProvider;
import works.hop.jetty.websocket.JettyWsServlet;
import works.hop.route.Routing;

import javax.servlet.DispatcherType;
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
        String pathSpec = mapping.endsWith("/*") ? mapping : (mapping.endsWith("/") ? mapping.concat("*") : mapping.concat("/*"));
        ServletHolder defaultServlet = AppAssetsHandlers.createResourceServlet(properties, folder);
        servletContext.addServlet(defaultServlet, pathSpec);
        return this;
    }

    public ContextHandlerBuilder websocket(String path, JettyWsProvider provider, JettyWsPolicy policy) {
        String pathSpec = path.endsWith("/*") ? path : (path.endsWith("/")? path.substring(0, path.length() - 1) : path.concat("/*"));
        ServletHolder handlerHolder = new ServletHolder("websocket-".concat(path), new JettyWsServlet(provider, policy.getPolicy()));
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
