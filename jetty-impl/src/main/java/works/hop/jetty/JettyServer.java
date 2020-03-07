package works.hop.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import works.hop.core.Handler;
import works.hop.core.ServerApi;

import java.util.concurrent.CompletableFuture;

public class JettyServer extends ServerApi {

    private final Server server;
    private final ServerConnector connector;
    private final ContextHandlerCollection contexts = new ContextHandlerCollection();

    private JettyServer() {
        this.server = new Server();
        //add connector
        this.connector = new ServerConnector(server);
        //set contexts handler
        this.server.setHandler(contexts);
    }

    public void addHandler(String path, AbstractHandler handler) {
        ContextHandler context = new ContextHandler(path);
        context.setHandler(handler);
        contexts.addHandler(context);
    }

    public void addHandler(String method, String path, Handler handler) {
        JettyHandler handle = new JettyHandler(handler);
        ContextHandler context = new ContextHandler(path);
        context.setHandler(handle);
        contexts.addHandler(context);
    }

    public static JettyServer createServer() throws Exception {
        JettyServer server = new JettyServer();
        return server;
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String host = "localhost";
        JettyServer server = createServer();
        server.addHandler("all", "/", (request, response) -> CompletableFuture.completedFuture("Hello Universe"));
        server.listen(port, host);
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
