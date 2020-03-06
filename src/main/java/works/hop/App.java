package works.hop;

import java.util.concurrent.CompletableFuture;

public class App implements Rest{

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    @Override
    public <T> CompletableFuture<ResponseEntity> get(String url, Headers headers, Request request, Response response, CompletableFuture<T> future) {
        return future.handle((data, th) -> {
            if (th == null) {
                return ResponseEntity.ok(data);
            } else {
                return ResponseEntity.error(th);
            }
        });
    }
}
