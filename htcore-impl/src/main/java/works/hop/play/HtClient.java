package works.hop.play;

import org.apache.http.*;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.message.BasicHttpRequest;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class HtClient {

    public static void main(String[] args) throws IOException, HttpException {
        int which = Integer.parseInt(Optional.ofNullable(args[0]).orElse("1"));
        switch (which) {
            case 1:
            case 2:
                exampleClient1();
                break;
            default:
                System.out.println("select 1 or 2");
        }
    }

    public static void exampleClient1() throws IOException, HttpException {
        int port = 8080;
        String host = "localhost";
        try (Socket socket = new Socket(host, port)) {
            DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
            conn.bind(socket);

            HttpRequest request = new BasicHttpRequest("GET", "/");
            conn.sendRequestHeader(request);

            System.out.println(conn.isOpen());
            HttpConnectionMetrics metrics = conn.getMetrics();
            System.out.println(metrics.getRequestCount());
            System.out.println(metrics.getResponseCount());
            System.out.println(metrics.getReceivedBytesCount());
            System.out.println(metrics.getSentBytesCount());

            HttpResponse response = conn.receiveResponseHeader();
            conn.receiveResponseEntity(response);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //do something useful with the result here
                entity.writeTo(System.out);
            }
        }
    }
}
