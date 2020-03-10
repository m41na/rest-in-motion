package works.hop.jetty;

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
import works.hop.route.MethodRouter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public static JettyStartable createServer(Map<String, String> props) {
        Properties locals = new Properties();
        props.forEach((key, value) -> locals.setProperty(key, value));
        Function<String, String> properties = key -> locals.getProperty(key);
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        JettyRouter router = new JettyRouter(new MethodRouter());
        JettyStartable server = new JettyStartable(properties, contexts, router, new Properties());
        return server;
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
