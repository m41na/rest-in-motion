package works.hop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class AppTest {

    App server = new App();

    @Test
    public void getRequestOnContextPathShouldReturnHello() {
        CompletableFuture<ResponseEntity> reply = server.get("/", new Headers(), new Request(), new Response(), CompletableFuture.completedFuture("Hello"));
        ResponseEntity result = reply.join();
        assertEquals("Expecting Hello", "Hello", result.data.toString());
        assertEquals("Expecting 0", 0, result.errors.size());
    }
}
