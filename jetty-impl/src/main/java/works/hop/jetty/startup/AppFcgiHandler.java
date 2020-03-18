package works.hop.jetty.startup;

import org.eclipse.jetty.fcgi.server.proxy.FastCGIProxyServlet;
import org.eclipse.jetty.fcgi.server.proxy.TryFilesFilter;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class AppFcgiHandler {

    private AppFcgiHandler() {
        throw new UnsupportedOperationException("You should not instantiate this class");
    }

    public static Map<String, String> defaultFcgProperties(String context, String home, String proxyTo) {
        Map<String, String> fcgiContext = new HashMap<>();
        fcgiContext.put("contextPath", context);
        fcgiContext.put("resourceBase", home);
        fcgiContext.put("welcomeFile", "index.php");
        fcgiContext.put("init.proxyTo", proxyTo);
        fcgiContext.put("init.scriptRoot", home);
        fcgiContext.put("init.dirAllowed", "true");
        return fcgiContext;
    }

    public static ServletContextHandler createFcgiHandler(Map<String, String> phpctx) {
        ServletContextHandler php_ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        php_ctx.setContextPath(phpctx.getOrDefault("contextPath", "/"));
        php_ctx.setResourceBase(phpctx.get("resourceBase"));
        php_ctx.setWelcomeFiles(new String[]{phpctx.get("welcomeFile")});

        // add try filter
        FilterHolder tryHolder = new FilterHolder(new TryFilesFilter());
        tryHolder.setInitParameter("files", phpctx.getOrDefault("init.files", "$path /index.php?p=$path"));
        php_ctx.addFilter(tryHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

        // Add default servlet (dest serve the html/css/js)
        ServletHolder defHolder = new ServletHolder("default", new DefaultServlet());
        defHolder.setInitParameter("dirAllowed", phpctx.getOrDefault("init.dirAllowed", "false"));
        php_ctx.addServlet(defHolder, phpctx.getOrDefault("assetsContext", "/"));

        // add fcgi servlet for php scripts
        ServletHolder fgciHolder = new ServletHolder("fcgi", new FastCGIProxyServlet());
        fgciHolder.setInitParameter("proxyTo", phpctx.get("init.proxyTo"));
        fgciHolder.setInitParameter("prefix", phpctx.getOrDefault("init.prefix", "/"));
        fgciHolder.setInitParameter("scriptRoot", phpctx.get("init.scriptRoot"));
        fgciHolder.setInitParameter("scriptPattern", phpctx.getOrDefault("init.scriptPattern", "(.+?\\\\.php)"));
        php_ctx.addServlet(fgciHolder, "*.php");
        return php_ctx;
    }
}
