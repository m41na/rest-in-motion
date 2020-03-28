package works.hop.ref;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;

import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws Exception {
        int port = 8090;
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        Server server = createServer(port, new PathResource(tempDir));

        server.start();
        server.dumpStdErr();
        server.join();
    }

    public static Server createServer(int port, Resource baseResource) {
        Server server = new Server();

        // HTTP connector
        ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(port);
        http.setIdleTimeout(30000);
        // Set the connector
        server.addConnector(http);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setBaseResource(baseResource);
        server.setHandler(context);

        context.addServlet(DefaultServlet.class, "/");
        ServletHolder graphQLHolder = new ServletHolder("graphql", LinksGraphQL.class);
        context.addServlet(graphQLHolder, "/graphql/*");
        return server;
    }
}
