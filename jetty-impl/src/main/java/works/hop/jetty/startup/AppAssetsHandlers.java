package works.hop.jetty.startup;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.function.Function;

public class AppAssetsHandlers {

    public ResourceHandler createResourceHandler(Function<String, String> properties, String resourceBase) {
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

    public ServletHolder createResourceServlet(Function<String, String> properties, String resourceBase) {
        // DefaultServlet should be named 'default-${resourceBase}'
        ServletHolder defaultServlet = new ServletHolder("default-" + resourceBase, DefaultServlet.class);
        defaultServlet.setInitParameter("resourceBase", resourceBase);
        defaultServlet.setInitParameter("dirAllowed", properties.apply("assets.dirAllowed"));
        defaultServlet.setInitParameter("pathInfoOnly", properties.apply("assets.pathInfoOnly"));
        defaultServlet.setInitParameter("etags", properties.apply("assets.etags"));
        defaultServlet.setInitParameter("acceptRanges", properties.apply("assets.acceptRanges"));
        defaultServlet.setInitParameter("cacheControl", properties.apply("assets.cacheControl"));
        defaultServlet.setInitParameter("welcomeFile", properties.apply("assets.welcomeFile"));
        return defaultServlet;
    }
}
