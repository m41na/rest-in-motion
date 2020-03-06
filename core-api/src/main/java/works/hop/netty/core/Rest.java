package works.hop.netty.core;

import java.util.concurrent.CompletableFuture;

public interface Rest {

    <T> CompletableFuture<ResponseEntity> all(String method, String url, CompletableFuture<Exchange> future, Handler<T> handler);

    <T> CompletableFuture<ResponseEntity> get(String url, CompletableFuture<Exchange> future, Handler<T> handler);

    <T> CompletableFuture<ResponseEntity> post(String url, CompletableFuture<Exchange> future, Handler<T> handler);

    <T> CompletableFuture<ResponseEntity> put(String url, CompletableFuture<Exchange> future, Handler<T> handler);

    <T> CompletableFuture<ResponseEntity> delete(String url, CompletableFuture<Exchange> future, Handler<T> handler);

    void start();

    void shutdown();

    void listen(Integer port, String host);
}
