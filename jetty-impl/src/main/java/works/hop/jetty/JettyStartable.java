package works.hop.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.core.RestMethods;
import works.hop.core.Restful;
import works.hop.core.StartableRest;
import works.hop.jetty.builder.ContextHandlerBuilder;
import works.hop.jetty.startup.AppConnectors;
import works.hop.jetty.startup.AppThreadPool;
import works.hop.route.MethodRouter;
import works.hop.route.Routing;
import works.hop.traverse.Visitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class JettyStartable implements StartableRest {

    public static final Logger LOG = LoggerFactory.getLogger(JettyStartable.class);

    private final Restful restful;
    private String status = "stopped";
    private Consumer<Boolean> shutdown;

    public JettyStartable(Restful restful) {
        this.restful = restful;
    }

    public static JettyStartable createServer(Map<String, String> props) {
        Function<String, String> properties = key -> props.get(key);
        JettyRouter router = new JettyRouter(new MethodRouter());
        Restful restful = new JettyRestful(properties, router);
        JettyStartable server = new JettyStartable(restful);
        return server;
    }

    public static JettyStartable createServer(String context, Map<String, String> props) {
        JettyStartable server = createServer(props);
        server.mount(context);
        return server;
    }

    public static JettyStartable createServer(String context, Map<String, String> props, Function<ContextHandlerBuilder, ServletContextHandler> builder) {
        JettyStartable server = createServer(props);
        server.mount(context, builder);
        return server;
    }

    @Override
    public JettyRestful rest() {
        return (JettyRestful) this.restful;
    }

    @Override
    public String status() {
        return "server status is " + status;
    }

    @Override
    public void banner() {
        try (InputStream is = getClass().getResourceAsStream(restful.properties().apply("splash"))) {
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
        try {
            status = "starting";
            // splash banner
            banner();
            // create server with thread pool
            QueuedThreadPool threadPool = AppThreadPool.createThreadPool(restful.properties());
            Server server = new Server(threadPool);

            // Scheduler
            server.addBean(new ScheduledExecutorScheduler());
            // HTTP Configuration
            HttpConfiguration httpConfig = AppConnectors.createHttpConfiguration(restful.properties());
            ServerConnector http2Connector = AppConnectors.configureHttpsConnector(restful.properties(), server, host, port, httpConfig);
            server.addConnector(http2Connector);

            // Add the ResourceHandler to the server.
            rest().rootHandlers.add(rest().contextHandlers);
            HandlerList handlerList = new HandlerList();
            handlerList.setHandlers(rest().rootHandlers.toArray(new Handler[0]));
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

    @Override
    public Function<String, String> properties() {
        return restful.properties();
    }

    @Override
    public Restful mount(String path) {
        return restful.mount(path);
    }

    public Restful mount(String path, Function<ContextHandlerBuilder, ServletContextHandler> builder) {
        return ((JettyRestful) restful).mount(path, builder);
    }

    @Override
    public Restful session(Function<String, String> properties) {
        return this.restful.session(properties);
    }

    @Override
    public Restful fcgi(String context, String home, String proxyTo) {
        return this.restful.fcgi(context, home, proxyTo);
    }

    @Override
    public void traverse(Visitor<Routing.Router> visitor) {
        this.restful.traverse(visitor);
    }

    @Override
    public RestMethods assets(String folder) {
        return this.restful.assets(folder);
    }

    @Override
    public RestMethods assets(String mapping, String folder) {
        return this.restful.assets(mapping, folder);
    }

    @Override
    public Routing.Router getRouter() {
        return this.restful.getRouter();
    }

    @Override
    public String getContext() {
        return this.restful.getContext();
    }
}
