package works.hop.jetty.servlet;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import java.util.function.Function;

public class ServletHandlerBuilder {

    private ServletContextHandler servletContext;

    private ServletHandlerBuilder() {
        this.servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
    }

    public static ServletHandlerBuilder newServlet() {
        return new ServletHandlerBuilder();
    }

    public ServletHandlerBuilder context(String path) {
        this.servletContext.setContextPath(path);
        return this;
    }

    public ServletHandlerBuilder assets(String mapping, Function<ResourceServletBuilder, ServletHolder> builder) {
        String pathSpec = mapping.endsWith("/*") ? mapping.substring(0, mapping.length() - 1) : (mapping.endsWith("/") ? mapping : mapping.concat("/"));
        ServletHolder defaultServlet = builder.apply(ResourceServletBuilder.newDefaultServlet());
        servletContext.setContextPath(pathSpec);
        servletContext.addServlet(defaultServlet, pathSpec);
        return this;
    }

    public ServletHandlerBuilder servlet(String path, ServletConfig config, HttpServlet handler) {
//        Routing.Route route = new Routing.Route(resolve(path), "all", "*", "*");
//        route.setId();
//        routes.add(route);
        // add servlet handler
        ServletHolder holder = new ServletHolder(handler);
        if (config != null) config.configure(holder);
        servletContext.addServlet(holder, path);
        return this;
    }

    public ServletContextHandler build() {
        return this.servletContext;
    }

    public static class ResourceServletBuilder {

        private ServletHolder defaultServlet;

        private ResourceServletBuilder() {
            this.defaultServlet = new ServletHolder(Long.valueOf(System.nanoTime()).toString(), DefaultServlet.class);
        }

        public static ResourceServletBuilder newDefaultServlet() {
            return new ResourceServletBuilder();
        }

        public ResourceServletBuilder resourceBase(String resourceBase) {
            this.defaultServlet.setInitParameter("resourceBase", resourceBase);
            return this;
        }

        public ResourceServletBuilder dirAllowed(String dirAllowed) {
            this.defaultServlet.setInitParameter("dirAllowed", dirAllowed);
            return this;
        }

        public ResourceServletBuilder pathInfoOnly(String pathInfoOnly) {
            this.defaultServlet.setInitParameter("etags", pathInfoOnly);
            return this;
        }

        public ResourceServletBuilder etags(String etags) {
            this.defaultServlet.setInitParameter("etags", etags);
            return this;
        }

        public ResourceServletBuilder acceptRanges(String acceptRanges) {
            this.defaultServlet.setInitParameter("acceptRanges", acceptRanges);
            return this;
        }

        public ResourceServletBuilder cacheControl(String cacheControl) {
            this.defaultServlet.setInitParameter("cacheControl", cacheControl);
            return this;
        }

        public ResourceServletBuilder welcomeFile(String welcomeFile) {
            this.defaultServlet.setInitParameter("welcomeFile", welcomeFile);
            return this;
        }

        public ResourceServletBuilder initParam(String param, String value) {
            this.defaultServlet.setInitParameter(param, value);
            return this;
        }

        public ServletHolder build() {
            return this.defaultServlet;
        }
    }
}
