package works.hop.netty.core;

import java.util.concurrent.CompletableFuture;

public interface Handler<T> {

    CompletableFuture<T> handle(Request request, Response response);
}
