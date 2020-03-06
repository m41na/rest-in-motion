package works.hop;

import java.util.concurrent.CompletableFuture;

public interface Handler<T> {

    CompletableFuture<T> handle(Headers headers, Request request, Response response, RequestEntity entity);
}
