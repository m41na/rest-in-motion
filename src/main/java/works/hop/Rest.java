package works.hop;

import java.util.concurrent.CompletableFuture;

public interface Rest {

    <T> CompletableFuture<ResponseEntity> get(String url, Headers headers, Request request, Response response, Handler<T> handler);

    <T> CompletableFuture<ResponseEntity> post(String url, Headers headers, Request request, Response response, RequestEntity entity, Handler<T> handler);

    <T> CompletableFuture<ResponseEntity> put(String url, Headers headers, Request request, Response response, RequestEntity entity, Handler<T> handler);
}
