package works.hop.play;

import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

public class TestingClients {

    @Test
    @Ignore("only enable when testing manually")
    public void testBasicServer() throws IOException {
        Socket socket = new Socket("localhost", 9090);
        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
        conn.bind(socket);
        System.out.println(conn.isOpen());
        HttpConnectionMetrics metrics = conn.getMetrics();
        System.out.println(metrics.getRequestCount());
        System.out.println(metrics.getResponseCount());
        System.out.println(metrics.getReceivedBytesCount());
    }

    @Test
    @Ignore("only enable when testing manually")
    public void testBlockingServer() throws IOException, HttpException {
        Socket socket = new Socket("localhost", 9090);
        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
        conn.bind(socket);
        HttpRequest request = new BasicHttpRequest("GET", "/");
        conn.sendRequestHeader(request);
        HttpResponse response = conn.receiveResponseHeader();
        conn.receiveResponseEntity(response);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // Do something useful with the entity and, when done, ensure all
            // content has been consumed, so that the underlying connection
            // can be re-used
            EntityUtils.consume(entity);
        }
    }

    @Test
    @Ignore("only enable when testing manually")
    public void testSimpleClient() throws IOException, HttpException {
        Socket socket = new Socket("localhost", 9090);
        DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(8 * 1024);
        conn.bind(socket);
        HttpRequest request = conn.receiveRequestHeader();
        if (request instanceof HttpEntityEnclosingRequest) {
            conn.receiveRequestEntity((HttpEntityEnclosingRequest) request);
            HttpEntity entity = ((HttpEntityEnclosingRequest) request)
                    .getEntity();
            if (entity != null) {
                // Do something useful with the entity and, when done, ensure all
                // content has been consumed, so that the underlying connection
                // could be re-used
                EntityUtils.consume(entity);
            }
        }
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
        response.setEntity(new StringEntity("Got it"));
        conn.sendResponseHeader(response);
        conn.sendResponseEntity(response);
    }
}
