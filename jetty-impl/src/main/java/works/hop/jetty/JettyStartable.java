package works.hop.jetty;

import org.apache.commons.cli.Options;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.core.Restful;
import works.hop.core.StartableRest;
import works.hop.jetty.session.SessionUtil;
import works.hop.jetty.startup.AppConnectors;
import works.hop.jetty.startup.AppFcgiHandler;
import works.hop.jetty.startup.AppThreadPool;
import works.hop.jetty.websocket.JettyWsAdapter;
import works.hop.route.MethodRouter;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static works.hop.jetty.startup.AppOptions.applyDefaults;

public class JettyStartable extends JettyRestful implements StartableRest {

    public static final Logger LOG = LoggerFactory.getLogger(JettyStartable.class);

    private final ContextHandlerCollection contexts;
    private final Function<String, String> properties;
    private String status = "stopped";
    private Consumer<Boolean> shutdown;

    public JettyStartable(Function<String, String> properties, ContextHandlerCollection contexts, JettyRouter router, Properties locals) {
        super(properties, router, locals);
        this.properties = properties;
        this.contexts = contexts;
    }

    public static JettyStartable createServer(Map<String, String> props) throws Exception {
        Properties locals = new Properties();
        props.forEach((key, value) -> locals.setProperty(key, value));
        Function<String, String> properties = key -> locals.getProperty(key);
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        JettyRouter router = new JettyRouter(new MethodRouter());
        JettyStartable server = new JettyStartable(properties, contexts, router, new Properties());
        return server;
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String host = "localhost";
        Map<String, String> props = applyDefaults(new Options(), args);
        JettyStartable server = createServer(props);
        server.get("/api", "*", "", emptyMap(), (request, response, promise) -> {
            response.ok(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date()));
            promise.complete();
        });
        server.assets(System.getProperty("user.dir"));
        server.websocket("/events/*", () -> new JettyWsAdapter());
        server.listen(port, host);
    }

    @Override
    public Restful rest() {
        return this;
    }

    @Override
    public String status() {
        return "server status is " + status;
    }

    @Override
    public void banner() {
        try (InputStream is = getClass().getResourceAsStream(properties.apply("splash"))) {
            if (is != null) {
                int maxSize = 1024;
                byte[] bytes = new byte[maxSize];
                int size = is.read(bytes);
                System.out.printf("splash file is %d bytes in size of a max acceptable %d bytes%n", size, maxSize);
                LOG.info(new String(bytes, 0, size));
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void start() throws Exception {
        //currently not in use since the 'listen' method starts the server
    }

    @Override
    public void shutdown() throws Exception {
        this.shutdown.accept(true);
    }

    @Override
    public void listen(Integer port, String host) {
        listen(port, host, null);
    }

    @Override
    public void listen(Integer port, String host, Consumer<String> result) {
        AppConnectors connectors = new AppConnectors();
        AppThreadPool threadPools = new AppThreadPool();
        AppFcgiHandler fcgiHandler = new AppFcgiHandler();

        try {
            status = "starting";
            // splash banner
            banner();

            // create server with thread pool
            QueuedThreadPool threadPool = threadPools.createThreadPool(this.properties);
            Server server = new Server(threadPool);

            // Scheduler
            server.addBean(new ScheduledExecutorScheduler());

            // HTTP Configuration
            HttpConfiguration httpConfig = connectors.createHttpConfiguration(this.properties);
            ServerConnector http2Connector = connectors.configureHttpsConnector(this.properties, server, host, port, httpConfig);
            server.addConnector(http2Connector);

            //configure appctx context handler
            String appctx = this.properties.apply("appctx");
            contexts.addHandler(createRoutesHandler(appctx.endsWith("/*") ? appctx.substring(0, appctx.length() - 1) : appctx.endsWith("/") ? appctx : appctx.concat("/")));

            // add activated context handler (say, fcgi for php)
            if (Boolean.parseBoolean(this.fcgiContext.get("fcgi"))) {
                handlers.add(fcgiHandler.createFcgiHandler(this.fcgiContext));
            }

            //configure session handler if necessary
            SessionHandler sessionHandler = null;
            if (Boolean.parseBoolean(this.properties.apply("session.jdbc.enable"))) {
                String url = this.properties.apply("session.jdbc.url");
                String driver = this.properties.apply("session.jdbc.driver");
                sessionHandler = SessionUtil.sqlSessionHandler(driver, url);
                handlers.add(sessionHandler);
            }

            //finally add the context handlers
            handlers.add(servlets);
            handlers.add(contexts);

            // Add the ResourceHandler to the server.
            HandlerList handlerList = new HandlerList();
            handlerList.setHandlers(handlers.toArray(new Handler[0]));
            server.setHandler(handlerList);

            //***** BEGIN EXPERIMENT *****//
//            ServletContextHandler servletsContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
//            servletsContext.setContextPath("/ser");
//            servletsContext.addServlet(HelloServlet.class, "/hello/*");
//
////            ContextHandler context = new ContextHandler("/");
////            context.setContextPath("/");
////            context.setHandler(new HelloHandler("Root Hello"));
//
//            ContextHandler contextFR = new ContextHandler("/fr");
//            contextFR.setHandler(new HelloHandler("Bonjour"));
//            ContextHandlerCollection handlerCollection = new ContextHandlerCollection(contextFR, createRoutesHandler("/wow"));
//
//            Path userDir = Paths.get(System.getProperty("user.dir"));
//            PathResource pathResource = new PathResource(userDir);
//            ResourceHandler resourceHandler = new ResourceHandler();
//
//            // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
//            // In this example it is the current directory but it can be configured to anything that the jvm has access to.
//            resourceHandler.setDirectoriesListed(true);
//            resourceHandler.setWelcomeFiles(new String[]{"index.html"});
//            resourceHandler.setBaseResource(pathResource);
//
//            // Add the ResourceHandler to the server.
//            HandlerList handlerList = new HandlerList();
//            handlerList.setHandlers(new Handler[]{resourceHandler, servletsContext, handlerCollection});
//            server.setHandler(handlerList);
//
//            route("get", "/api", "text/html", "*", (req, res, done) -> {
//                res.send("wow handler!");
//                done.complete();
//            });
            //***** END EXPERIMENT *****//

            // add shutdown handler
            shutdown = (flag) -> {
                try {
                    if (flag && server.isRunning()) server.stop();
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    System.exit(1);
                }
            };

            // add shutdown hook
            addRuntimeShutdownHook(server);

            //fire up server
            server.start();
            status = "running";

            //acknowledge about server running
            if (result != null)
                result.accept(String.format("AppServer is now listening on http://%s:%d. (Press CTRL+C to quit)", host, port));

            //if reached, join and await interruption
            server.join();
            status = "stopped";
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void addRuntimeShutdownHook(final Server server) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server.isStarted()) {
                server.setStopAtShutdown(true);
                try {
                    server.stop();
                } catch (Exception e) {
                    LOG.error("Error while shutting down jetty server", e);
                    throw new RuntimeException(e);
                }
            }
        }));
    }
}
