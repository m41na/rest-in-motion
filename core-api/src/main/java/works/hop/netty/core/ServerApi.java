package works.hop.netty.core;

import java.util.concurrent.CompletableFuture;

public class ServerApi implements Rest {

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    @Override
    public <T> CompletableFuture<ResponseEntity> all(String method, String url, CompletableFuture<Exchange> future, Handler<T> handler) {
        return future.thenCompose(exchange -> handler.handle(exchange.request(), exchange.response()).handle((data, th) -> {
            if (th == null) {
                return ResponseEntity.ok(data);
            } else {
                return ResponseEntity.error(th);
            }
        }));
    }

    @Override
    public <T> CompletableFuture<ResponseEntity> get(String url, CompletableFuture<Exchange> future, Handler<T> handler) {
        return all("get", url, future, handler);
    }

    @Override
    public <T> CompletableFuture<ResponseEntity> post(String url, CompletableFuture<Exchange> future, Handler<T> handler) {
        return all("post", url, future, handler);
    }

    @Override
    public <T> CompletableFuture<ResponseEntity> put(String url, CompletableFuture<Exchange> future, Handler<T> handler) {
        return all("put", url, future, handler);
    }

    @Override
    public <T> CompletableFuture<ResponseEntity> delete(String url, CompletableFuture<Exchange> future, Handler<T> handler) {
        return all("delete", url, future, handler);
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void listen(Integer port, String host) {

    }
}
