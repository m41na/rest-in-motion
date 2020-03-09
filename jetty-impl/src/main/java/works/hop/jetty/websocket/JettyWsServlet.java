package works.hop.jetty.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.util.Collections;
import java.util.Map;

public class JettyWsServlet extends WebSocketServlet {

    private static final long serialVersionUID = 1L;
    private final JettyWsProvider provider;
    private final Map<String, Long> policy;

    public JettyWsServlet(JettyWsProvider provider) {
        this.provider = provider;
        this.policy = Collections.emptyMap();
    }

    public JettyWsServlet(JettyWsProvider provider, Map<String, Long> policy) {
        this.provider = provider;
        this.policy = policy;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        if (policy.containsKey(JettyWsPolicy.IDLE_TIMEOUT)) {
            factory.getPolicy().setIdleTimeout(policy.get(JettyWsPolicy.IDLE_TIMEOUT));
        }
        if (policy.containsKey(JettyWsPolicy.MAX_BINARY_MESSAGE_BUFFER_SIZE)) {
            factory.getPolicy().setMaxBinaryMessageBufferSize(policy.get(JettyWsPolicy.MAX_BINARY_MESSAGE_BUFFER_SIZE).intValue());
        }
        if (policy.containsKey(JettyWsPolicy.MAX_BINARY_MESSAGE_SIZE)) {
            factory.getPolicy().setMaxBinaryMessageSize(policy.get(JettyWsPolicy.MAX_BINARY_MESSAGE_SIZE).intValue());
        }
        if (policy.containsKey(JettyWsPolicy.MAX_TEXT_MESSAGE_BUFFER_SIZE)) {
            factory.getPolicy().setMaxTextMessageBufferSize(policy.get(JettyWsPolicy.MAX_TEXT_MESSAGE_BUFFER_SIZE).intValue());
        }
        if (policy.containsKey(JettyWsPolicy.MAX_TEXT_MESSAGE_SIZE)) {
            factory.getPolicy().setMaxTextMessageSize(policy.get(JettyWsPolicy.MAX_TEXT_MESSAGE_SIZE).intValue());
        }
        // factory.register(AppWebSocket.class);
        factory.setCreator(new JettyWsCreator(provider));
    }
}
