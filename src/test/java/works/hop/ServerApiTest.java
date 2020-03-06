package works.hop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class ServerApiTest {

    private ServerApi server;

    @Before
    public void setUp() {
        server = new ServerApi();
    }

    @Test
    public void getRequestOnContextPathShouldReturn200Ok() {
        CompletableFuture<Exchange> future = CompletableFuture.supplyAsync(() -> new Exchange());
        CompletableFuture<ResponseEntity> reply = server.get("/", future, (request, response) -> CompletableFuture.completedFuture("Hello"));
        ResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }

    @Test
    public void postRequestOnContextPathShouldReturn200Ok() {
        CompletableFuture<Exchange> future = CompletableFuture.supplyAsync(() -> new Exchange());
        CompletableFuture<ResponseEntity> reply = server.delete("/", future, (request, response) -> CompletableFuture.completedFuture("Hello"));
        ResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }

    @Test
    public void putRequestOnContextPathShouldReturn200Ok() {
        CompletableFuture<Exchange> future = CompletableFuture.supplyAsync(() -> new Exchange());
        CompletableFuture<ResponseEntity> reply = server.delete("/", future, (request, response) -> CompletableFuture.completedFuture("Hello"));
        ResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }

    @Test
    public void deleteRequestOnContextPathShouldReturn200Ok() {
        CompletableFuture<Exchange> future = CompletableFuture.supplyAsync(() -> new Exchange());
        CompletableFuture<ResponseEntity> reply = server.delete("/", future, (request, response) -> CompletableFuture.completedFuture("Hello"));
        ResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }
}
