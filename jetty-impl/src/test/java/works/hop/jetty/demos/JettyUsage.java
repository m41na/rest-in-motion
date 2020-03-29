package works.hop.jetty.demos;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.resource.PathResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static javax.servlet.DispatcherType.ASYNC;
import static javax.servlet.DispatcherType.REQUEST;

public class JettyUsage {

    public static Consumer<String[]> OneHandler = args -> {
        int port = 8080;
        // The Server
        Server server = createServer(port);

        // Set a handler
        server.setHandler(new HelloHandler());

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    };

    public static Consumer<String[]> AssetsHandler = args -> {
        int port = 8080;

        // The Server
        Server server = createServer(port);

        // The ResourceHandler
        Path userDir = Paths.get(System.getProperty("user.dir"));
        ResourceHandler resourceHandler = createResourceHandler(userDir);

        // Add the ResourceHandler to the server.
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, new DefaultHandler()});
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    };

    public static Consumer<String[]> OneServletHandler = args -> {
        int port = 8080;
        // The Server
        Server server = createServer(port);

        // The JettyServlet
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        // Passing in the class for the Servlet allows jetty to instantiate an
        // instance of that Servlet and mount it on a given context path.

        // IMPORTANT:
        // This is a raw Servlet, not a Servlet that has been configured
        // through a web.xml, @WebServlet annotation, or anything similar.
        handler.addServletWithMapping(HelloServlet.class, "/*");

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    };

    public static Consumer<String[]> OneContextHandler = args -> {
        int port = 8080;
        // The Server
        Server server = createServer(port);

        // Add a single handler on context "/hello"
        ContextHandler context = new ContextHandler();
        context.setContextPath("/hello");
        context.setHandler(new HelloHandler());

        // Can be accessed using http://localhost:8080/hello

        // Set a handler
        server.setHandler(context);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    };

    public static Consumer<String[]> MultipleContextHandlers = args -> {
        int port = 8080;
        // The Server
        Server server = createServer(port);

        // Multiple handlers
        ContextHandler context = new ContextHandler("/");
        context.setContextPath("/");
        context.setHandler(new HelloHandler("Root Hello"));

        ContextHandler contextFR = new ContextHandler("/fr");
        contextFR.setHandler(new HelloHandler("Bonjour"));

        ContextHandler contextIT = new ContextHandler("/it");
        contextIT.setHandler(new HelloHandler("Buongiorno"));

        ContextHandler contextV = new ContextHandler("/");
        contextV.setVirtualHosts(new String[]{"127.0.0.2" });
        contextV.setHandler(new HelloHandler("Virtual Hello"));

        ContextHandlerCollection contexts = new ContextHandlerCollection(
                context, contextFR, contextIT, contextV
        );

        // Set a handler
        server.setHandler(contexts); //make sure the handler is 'contexts', not the singular 'context'

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    };

    public static Consumer<String[]> MultipleContextHandlersWithResourceHandler = args -> {
        int port = 8080;
        // The Server
        Server server = createServer(port);

        // Multiple handlers
        ContextHandler context = new ContextHandler("/");
        context.setContextPath("/");
        context.setHandler(new HelloHandler("Root Hello"));

        ContextHandler contextFR = new ContextHandler("/fr");
        contextFR.setHandler(new HelloHandler("Bonjour"));

        ContextHandler contextIT = new ContextHandler("/it");
        contextIT.setHandler(new HelloHandler("Buongiorno"));

        ContextHandler contextV = new ContextHandler("/");
        contextV.setVirtualHosts(new String[]{"127.0.0.2" });
        contextV.setHandler(new HelloHandler("Virtual Hello"));

        ContextHandlerCollection contexts = new ContextHandlerCollection(
                context, contextFR, contextIT, contextV
        );

        // Set a handler
        Path userDir = Paths.get(System.getProperty("user.dir"));
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[]{/*createResourceHandler(userDir),*/ contexts, new DefaultHandler()});
        server.setHandler(handlerList);
        //observations:
        // 1. the position of the ResourceHandler in the handlerList determines the view rendered for '/' path
        // 2. having ResourceHandler outside of 'contexts' allows you to have both a ResourceHandler and ContextHandler at the '/' path

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    };

    public static Consumer<String[]> OneServletContext = args -> {
        int port = 8080;
        // The Server
        Server server = createServer(port);

        // Resource folder
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        //Servlet context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setBaseResource(new PathResource(tempDir));

        // add hello servlet
        context.addServlet(HelloServlet.class, "/hello/*");

        // Add dump servlet on multiple url-patterns
        ServletHolder dumpHolder = new ServletHolder("debug", DumpServlet.class);
        context.addServlet(dumpHolder, "/dump/*");
        context.addServlet(dumpHolder, "*.dump");

        // add default servlet (for error handling and static resources)
        context.addServlet(DefaultServlet.class, "/");

        // sprinkle in a few filters to demonstrate behaviors
        context.addFilter(HelloFilter.class, "/test/*", EnumSet.of(REQUEST));
        context.addFilter(HelloFilter.class, "*.test", EnumSet.of(REQUEST, ASYNC));

        // and a few listeners to show other ways of working with servlets
        context.getServletHandler().addListener(new ListenerHolder(HelloServletListener.class));
        context.getServletHandler().addListener(new ListenerHolder(HelloRequestListener.class));

        // Set a handler
        server.setHandler(context);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    };
    public static Consumer<String[]> MultipleMixedBagOfContextHandlers = args -> {
        int port = 8080;
        // The Server
        Server server = createServer(port);

        //collection of contexts
        ContextHandlerCollection contexts = new ContextHandlerCollection();

        // Resource folder
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        //Servlet context
        ServletContextHandler context1 = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context1.setContextPath("/");
        context1.setBaseResource(new PathResource(tempDir));

        // add hello servlet
        context1.addServlet(HelloServlet.class, "/hello/*");

        // Add dump servlet on multiple url-patterns
        ServletHolder dumpHolder = new ServletHolder("debug", DumpServlet.class);
        context1.addServlet(dumpHolder, "/dump/*");

        // add default servlet (for error handling and static resources)
        context1.addServlet(DefaultServlet.class, "/");
        contexts.addHandler(context1);

        //CREATE SECOND SET OF SERVLET CONTEXT
        // Resource folder
        Path userDir = Paths.get(System.getProperty("user.dir"));

        //Servlet context
        ServletContextHandler context2 = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context2.setContextPath("/2");
        context2.setBaseResource(new PathResource(userDir));

        // add hello servlet
        context2.addServlet(HelloServlet.class, "/hello/*");

        // Add dump servlet on multiple url-patterns
        ServletHolder dumpHolder2 = new ServletHolder("debug", DumpServlet.class);
        context2.addServlet(dumpHolder2, "/dump/*");

        // add default servlet (for error handling and static resources)
        context2.addServlet(DefaultServlet.class, "/");
        contexts.addHandler(context2);

        //ADD THIRD/FOURTH SET OF CONTEXT HANDLERS
        ContextHandler contextFR = new ContextHandler("/fr");
        contextFR.setHandler(new HelloHandler("Bonjour"));
        contexts.addHandler(contextFR);

        ContextHandler contextIT = new ContextHandler("/it");
        contextIT.setHandler(new HelloHandler("Buongiorno"));
        contexts.addHandler(contextIT);

        //Add handlers
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{contexts, new DefaultHandler()});
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    };

    public static void main(String[] args) {
        Map<Integer, Consumer> targets = new HashMap<>();
        targets.put(1, OneHandler);
        targets.put(2, AssetsHandler);
        targets.put(3, OneServletHandler);
        targets.put(4, OneContextHandler);
        targets.put(5, MultipleContextHandlers);
        targets.put(6, MultipleContextHandlersWithResourceHandler);
        targets.put(7, OneServletContext);
        targets.put(7, MultipleMixedBagOfContextHandlers);

        //execute from map
        targets.get(7).accept(args);
    }

    public static Server createServer(int port) {
        // The Server
        Server server = new Server();

        // HTTP connector
        ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(port);
        http.setIdleTimeout(30000);

        // Set the connector
        server.addConnector(http);
        return server;
    }

    public static ResourceHandler createResourceHandler(Path folder) {
        PathResource pathResource = new PathResource(folder);
        ResourceHandler resourceHandler = new ResourceHandler();

        // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
        // In this example it is the current directory but it can be configured to anything that the jvm has access to.
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html" });
        resourceHandler.setBaseResource(pathResource);
        return resourceHandler;
    }
}
