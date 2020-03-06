package works.hop;

import java.util.concurrent.CompletableFuture;

public interface Handler<T> {

    CompletableFuture<ResponseEntity> handle(Headers headers, Request request, Response response);
}
