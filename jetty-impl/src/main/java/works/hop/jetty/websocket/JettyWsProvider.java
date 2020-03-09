package works.hop.jetty.websocket;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public interface JettyWsProvider<T extends WebSocketAdapter>  {

    T provide();
}
