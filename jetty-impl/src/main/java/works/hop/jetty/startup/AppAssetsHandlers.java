package works.hop.jetty.startup;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.function.Function;

public class AppAssetsHandlers {

    private static final Logger LOG = LoggerFactory.getLogger(AppAssetsHandlers.class);

    public void configureAssets(Function<String, String> properties, Collection<ContextHandler> contexts, ServletContextHandler servlets, String mapping, String folder) {
        if (!AppOptions.UNASSIGNED.equals(properties.apply("assets"))) {
            LOG.warn("To use this option, remove the 'assets' property from the initial properties object");
        } else {
            String pathSpec = mapping.endsWith("/*") ? mapping : (mapping.endsWith("/") ? mapping + "*" : mapping + "/*");
            if (Boolean.parseBoolean(properties.apply("assets.default.servlet"))) {
                ServletHolder defaultServlet = createResourceServlet(properties, folder);
                servlets.addServlet(defaultServlet, pathSpec);
            } else {
                ContextHandler context = new ContextHandler();
                context.setContextPath(pathSpec);
                context.setHandler(createResourceHandler(properties, folder));
                contexts.add(context);
            }
        }
    }

    private ResourceHandler createResourceHandler(Function<String, String> properties, String resourceBase) {
        ResourceHandler appResources = new ResourceHandler();
        appResources.setResourceBase(resourceBase);
        appResources.setDirectoriesListed(Boolean.parseBoolean(properties.apply("assets.dirAllowed")));
        appResources.setPathInfoOnly(Boolean.parseBoolean(properties.apply("assets.pathInfoOnly")));
        appResources.setEtags(Boolean.parseBoolean(properties.apply("assets.etags")));
        appResources.setAcceptRanges(Boolean.parseBoolean(properties.apply("assets.acceptRanges")));
        appResources.setCacheControl(properties.apply("assets.cacheControl"));
        appResources.setWelcomeFiles(new String[]{properties.apply("assets.welcomeFile")});
        return appResources;
    }

    private ServletHolder createResourceServlet(Function<String, String> properties, String resourceBase) {
        // DefaultServlet should be named 'default-${resourceBase}'
        ServletHolder defaultServlet = new ServletHolder("default-" + resourceBase, DefaultServlet.class);
        defaultServlet.setInitParameter("resourceBase", resourceBase);
        defaultServlet.setInitParameter("dirAllowed", properties.apply("assets.dir  Allowed"));
        defaultServlet.setInitParameter("pathInfoOnly", properties.apply("assets.pathInfoOnly"));
        defaultServlet.setInitParameter("etags", properties.apply("assets.etags"));
        defaultServlet.setInitParameter("acceptRanges", properties.apply("assets.acceptRanges"));
        defaultServlet.setInitParameter("cacheControl", properties.apply("assets.cacheControl"));
        defaultServlet.setInitParameter("welcomeFile", properties.apply("assets.welcomeFile"));
        return defaultServlet;
    }
}
