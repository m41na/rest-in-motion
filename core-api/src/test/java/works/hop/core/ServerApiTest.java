package works.hop.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;

public class ServerApiTest {

    private ServerApi server;
    @Mock
    private Exchange exchange;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        server = new ServerApi();
    }

    @Test
    public void getRequestOnContextPathShouldReturn200Ok() {
        CompletableFuture<Exchange> future = CompletableFuture.supplyAsync(() -> exchange);
        CompletableFuture<AResponseEntity> reply = server.get("/", future, (request, response) -> CompletableFuture.completedFuture("Hello"));
        AResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }

    @Test
    public void postRequestOnContextPathShouldReturn200Ok() {
        CompletableFuture<Exchange> future = CompletableFuture.supplyAsync(() -> exchange);
        CompletableFuture<AResponseEntity> reply = server.delete("/", future, (request, response) -> CompletableFuture.completedFuture("Hello"));
        AResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }

    @Test
    public void putRequestOnContextPathShouldReturn200Ok() {
        CompletableFuture<Exchange> future = CompletableFuture.supplyAsync(() -> exchange);
        CompletableFuture<AResponseEntity> reply = server.delete("/", future, (request, response) -> CompletableFuture.completedFuture("Hello"));
        AResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }

    @Test
    public void deleteRequestOnContextPathShouldReturn200Ok() {
        CompletableFuture<Exchange> future = CompletableFuture.supplyAsync(() -> exchange);
        CompletableFuture<AResponseEntity> reply = server.delete("/", future, (request, response) -> CompletableFuture.completedFuture("Hello"));
        AResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }
}
