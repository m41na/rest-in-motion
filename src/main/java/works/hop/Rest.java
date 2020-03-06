package works.hop;

import java.util.concurrent.CompletableFuture;

public interface Rest {

    <T> CompletableFuture<ResponseEntity> get(String url, Headers headers, Request request, Response response, CompletableFuture<T> future);
}
