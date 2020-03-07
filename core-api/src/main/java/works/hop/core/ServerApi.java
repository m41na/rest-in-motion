package works.hop.core;

import java.util.concurrent.CompletableFuture;

public class ServerApi implements Rest {

    @Override
    public <T> CompletableFuture<AResponseEntity> all(String method, String url, CompletableFuture<Exchange> future, Handler<T> handler) {
        return future.thenCompose(exchange -> handler.handle(exchange.request(), exchange.response()).handle((data, th) -> {
            if (th == null) {
                return AResponseEntity.ok(data);
            } else {
                return AResponseEntity.error(th);
            }
        }));
    }

    @Override
    public <T> CompletableFuture<AResponseEntity> get(String url, CompletableFuture<Exchange> future, Handler<T> handler) {
        return all("get", url, future, handler);
    }

    @Override
    public <T> CompletableFuture<AResponseEntity> post(String url, CompletableFuture<Exchange> future, Handler<T> handler) {
        return all("post", url, future, handler);
    }

    @Override
    public <T> CompletableFuture<AResponseEntity> put(String url, CompletableFuture<Exchange> future, Handler<T> handler) {
        return all("put", url, future, handler);
    }

    @Override
    public <T> CompletableFuture<AResponseEntity> delete(String url, CompletableFuture<Exchange> future, Handler<T> handler) {
        return all("delete", url, future, handler);
    }

    @Override
    public void start() throws Exception {
        throw new UnsupportedOperationException("Implement this in a subclass");
    }

    @Override
    public void shutdown() throws Exception {
        throw new UnsupportedOperationException("Implement this in a subclass");
    }

    @Override
    public void listen(Integer port, String host) throws Exception {
        throw new UnsupportedOperationException("Implement this in a subclass");
    }
}
