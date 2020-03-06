package works.hop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class AppTest {

    App server = new App();

    @Test
    public void getRequestOnContextPathShouldReturn200Ok() {
        CompletableFuture<ResponseEntity> reply = server.get("/", new Headers(), new Request(), new Response(), (h, req, res, body) -> CompletableFuture.completedFuture("Hello"));
        ResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }

    @Test
    public void postRequestOnContextPathShouldReturn200Ok() {
        CompletableFuture<ResponseEntity> reply = server.post("/",  new Headers(), new Request(), new Response(), new RequestEntity(), (h, req, res, body) -> CompletableFuture.completedFuture("Hello"));
        ResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }

    @Test
    public void putRequestOnContextPathShouldReturn200Ok() {
        CompletableFuture<ResponseEntity> reply = server.put("/",  new Headers(), new Request(), new Response(), new RequestEntity(), (h, req, res, body) -> CompletableFuture.completedFuture("Hello"));
        ResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }
}
