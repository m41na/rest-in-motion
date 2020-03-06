package works.hop;

import java.util.concurrent.CompletableFuture;

public class App implements Rest{

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    @Override
    public <T> CompletableFuture<ResponseEntity> get(String url,  Headers headers, Request request, Response response, Handler<T> handler) {
        return handler.handle(headers, request, response, null).handle((data, th) -> {
            if (th == null) {
                return ResponseEntity.ok(data);
            } else {
                return ResponseEntity.error(th);
            }
        });
    }

    @Override
    public <T> CompletableFuture<ResponseEntity> post(String url,  Headers headers, Request request, Response response, RequestEntity entity, Handler<T> handler) {
        return handler.handle(headers, request, response, entity).handle((data, th) -> {
            if (th == null) {
                return ResponseEntity.ok(data);
            } else {
                return ResponseEntity.error(th);
            }
        });
    }

    @Override
    public <T> CompletableFuture<ResponseEntity> put(String url, Headers headers, Request request, Response response, RequestEntity entity, Handler<T> handler) {
        return handler.handle(headers, request, response, entity).handle((data, th) -> {
            if (th == null) {
                return ResponseEntity.ok(data);
            } else {
                return ResponseEntity.error(th);
            }
        });
    }
}
