package works.hop.core;

import java.util.concurrent.CompletableFuture;

public interface Handler<T> {

    CompletableFuture<T> handle(ARequest ARequest, AResponse AResponse);
}
