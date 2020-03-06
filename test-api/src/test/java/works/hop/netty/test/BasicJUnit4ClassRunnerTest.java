package works.hop.netty.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import works.hop.netty.core.Exchange;
import works.hop.netty.core.ServerApi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;

@RunWith(BasicJUnit4ClassRunner.class)
@Ignore("work not complete - placeholder for testing later")
public class BasicJUnit4ClassRunnerTest {

    public static final Integer PORT = 9099;
    public static final String HOST = "127.0.0.1";

    @BasicProvider
    public ServerApi provider() {
        ServerApi server = new ServerApi();
        CompletableFuture<Exchange> future = CompletableFuture.supplyAsync(() -> new Exchange());
        server.get("/", future, (req, res) -> CompletableFuture.completedFuture("hello from server"));
        server.listen(PORT, HOST);
        return server;
    }

    @Test
    public void testHelloFromServer(HttpClient client) throws InterruptedException, IOException {
        String uri = String.format("http://%s:%d%s", HOST, PORT, "/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Contains 'hello from server'", true, response.body().contains("hello from server"));
    }
}