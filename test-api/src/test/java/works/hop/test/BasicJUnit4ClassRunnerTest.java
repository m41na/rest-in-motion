package works.hop.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import works.hop.core.ServerApi;
import works.hop.jetty.JettyServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static works.hop.jetty.JettyServer.createServer;

@RunWith(BasicJUnit4ClassRunner.class)
public class BasicJUnit4ClassRunnerTest {

    public static final Integer PORT = 8080;
    public static final String HOST = "127.0.0.1";
    public static final String BASE = "/";

    @BasicProvider
    public ServerApi provider() throws Exception {
        JettyServer server = createServer(BASE);
        server.get("/", "*", "", emptyMap(), (request, response, promise) -> {
            response.ok("hello from server");
            promise.complete();
        });
        server.listen(PORT, HOST);
        return server;
    }

    @Test
    public void testHelloFromServer(HttpClient client) throws InterruptedException, IOException {
        String uri = String.format("http://%s:%d%s", HOST, PORT, BASE);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Contains 'hello from server'", true, response.body().contains("hello from server"));
    }
}