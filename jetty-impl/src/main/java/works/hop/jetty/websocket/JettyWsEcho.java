package works.hop.jetty.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;

public class JettyWsEcho extends WebSocketAdapter {

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        try {
            this.onClose(getSession(), statusCode, reason);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        try {
            this.onConnect(getSession());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        try {
            this.onError(getSession(), cause);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        super.onWebSocketError(cause);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        try {
            this.onMessage(getSession(), message);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void onConnect(Session session) throws IOException {
        System.out.println(session.getRemoteAddress().getHostString() + "connected");
    }

    public void onClose(Session session, int status, String reason) throws IOException {
        System.out.println(session.getRemoteAddress().getHostString() + " closed");
    }

    public void onError(Session session, Throwable error) throws IOException {
        System.out.println(session.getRemoteAddress().getHostString() + " error - " + error.getMessage());
    }

    public void onMessage(Session session, String message) throws IOException {
        System.out.println("Message received - " + message);
        if (session.isOpen()) {
            String response = message.toUpperCase();
            session.getRemote().sendString(response);
        }
    }
}
