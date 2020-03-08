package works.hop.handler;

import works.hop.core.ARequest;
import works.hop.core.AResponse;

public interface Handler {

    void handle(ARequest ARequest, AResponse AResponse, HandlerPromise promise);
}
