package works.hop.jetty.websocket;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class JettyWsCreator implements WebSocketCreator {

    private final JettyWsProvider provider;

    public JettyWsCreator(JettyWsProvider provider) {
        this.provider = provider;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        return provider.provide();
    }
}
