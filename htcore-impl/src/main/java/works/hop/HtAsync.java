package works.hop;

import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.protocol.*;
import org.apache.http.protocol.*;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class HtAsync {

    static HttpProcessor httpProcessor = HttpProcessorBuilder.create()
            .add(new ResponseDate())
            .add(new ResponseServer("Rest-in-Async-Motion-HTTP/1.1"))
            .add(new ResponseContent())
            .add(new ResponseConnControl())
            // .add(new RequestExpectContinue(true))
            .build();

    static HttpAsyncRequestHandler<HttpRequest> requestHandler = new HttpAsyncRequestHandler<HttpRequest>() {

        public HttpAsyncRequestConsumer<HttpRequest> processRequest(
                final HttpRequest request,
                final HttpContext context) {
            // Buffer request content in memory for simplicity
            return new BasicAsyncRequestConsumer();
        }

        public void handle(
                final HttpRequest request,
                final HttpAsyncExchange httpExchange,
                final HttpContext context) throws HttpException, IOException {
            HttpResponse response = httpExchange.getResponse();
            response.setStatusCode(HttpStatus.SC_OK);
            response.setEntity(new StringEntity("message from async request handler",
                    ContentType.TEXT_PLAIN));
            httpExchange.submitResponse(new BasicAsyncResponseProducer(response));
        }

    };

    public static void example2() throws IOException, InterruptedException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        int port = 8080;
        SSLContext sslContext = null;
        if (port == 8443) {
            // Initialize SSL context
            final URL url = HtAsync.class.getResource("/test.keystore");
            if (url == null) {
                System.out.println("Keystore not found");
                System.exit(1);
            }
            System.out.println("Loading keystore " + url);
            sslContext = SSLContexts.custom()
                    .loadKeyMaterial(url, "nopassword".toCharArray(), "nopassword".toCharArray())
                    .build();
        }

        UriHttpAsyncRequestHandlerMapper handlerMapper = new UriHttpAsyncRequestHandlerMapper();
        handlerMapper.register("*", requestHandler);

        IOReactorConfig config = IOReactorConfig.custom()
                .setTcpNoDelay(true)
                .setSoTimeout(5000)
                .setSoReuseAddress(true)
                .setConnectTimeout(5000)
                .build();

        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .setServerInfo("Test/1.1")
                .setIOReactorConfig(config)
                //.setSslContext(sslContext)
                .setExceptionLogger(ExceptionLogger.STD_ERR)
                .setHandlerMapper(handlerMapper)
                .create();

        server.start();
        server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.shutdown(5, TimeUnit.SECONDS)));
    }

    public static void main(String[] args) throws IOException, InterruptedException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        int which = Integer.parseInt(Optional.ofNullable(args[0]).orElse("1"));
        switch (which) {
            case 1:
            case 2:
                example2();
                break;
            default:
                System.out.println("select 1 or 2");
        }
    }
}
