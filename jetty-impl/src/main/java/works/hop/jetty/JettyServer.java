package works.hop.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import works.hop.core.RestServer;
import works.hop.core.Restful;
import works.hop.route.MethodRouter;
import works.hop.route.Routing;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public class JettyServer extends RestServer {

    private final Server server;
    private final ServerConnector connector;
    private final ContextHandlerCollection contexts = new ContextHandlerCollection();
    private final JettyRouter router = new JettyRouter(new MethodRouter());

    private JettyServer(Function<String, String> properties) {
        super(properties);
        //create server
        this.server = new Server();
        //add connector
        this.connector = new ServerConnector(server);
        //add routing handler
        this.addHandler(properties.apply(APP_CTX_KEY));
        //set contexts handler
        this.server.setHandler(contexts);
    }

    public static JettyServer createServer(Map<String, String> props) throws Exception {
        Properties locals = new Properties();
        props.forEach((key, value) -> locals.setProperty(key, value));
        Function<String, String> properties = key -> locals.getProperty(key);

        JettyServer server = new JettyServer(properties);
        return server;
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String host = "localhost";
        Map<String, String> props = new HashMap<>();
        props.put(APP_CTX_KEY, "/");
        JettyServer server = createServer(props);
        server.get("/", "*", "", emptyMap(), (request, response, promise) -> {
            response.ok(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date()));
            promise.complete();
        });
        server.listen(port, host);
    }

    private void addHandler(String path) {
        JettyHandler handle = new JettyHandler(router);
        ContextHandler context = new ContextHandler(path);
        context.setHandler(handle);
        contexts.addHandler(context);
    }

    @Override
    public Restful upload(String path, String outputDir, Object configuration) throws IOException {
        UploadHandler handle = new UploadHandler(path, Paths.get(outputDir), (MultipartConfigElement) configuration);
        ContextHandler context = new ContextHandler(path);
        context.setHandler(handle);
        contexts.addHandler(context);
        return this;
    }

    @Override
    public Routing.Router getRouter() {
        return this.router;
    }

    @Override
    public void start() throws Exception {
        this.server.start();
        this.server.join();
    }

    @Override
    public void shutdown() throws Exception {
        this.server.stop();
    }

    @Override
    public void listen(Integer port, String host) throws Exception {
        this.connector.setHost(host);
        this.connector.setPort(port);
        this.connector.setIdleTimeout(30000);

        //set the connector
        server.addConnector(connector);
        this.start();
    }
}
