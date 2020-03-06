package works.hop.core;

import java.util.concurrent.CompletableFuture;

public interface Rest {

    <T> CompletableFuture<AResponseEntity> all(String method, String url, CompletableFuture<Exchange> future, Handler<T> handler);

    <T> CompletableFuture<AResponseEntity> get(String url, CompletableFuture<Exchange> future, Handler<T> handler);

    <T> CompletableFuture<AResponseEntity> post(String url, CompletableFuture<Exchange> future, Handler<T> handler);

    <T> CompletableFuture<AResponseEntity> put(String url, CompletableFuture<Exchange> future, Handler<T> handler);

    <T> CompletableFuture<AResponseEntity> delete(String url, CompletableFuture<Exchange> future, Handler<T> handler);

    void start() throws Exception;

    void shutdown() throws Exception;

    void listen(Integer port, String host) throws Exception;
}
