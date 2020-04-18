package works.hop;

import org.apache.http.*;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.*;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class HtBlocking {

    static HttpRequestHandler requestHandler = new HttpRequestHandler() {

        public void handle(
                HttpRequest request,
                HttpResponse response,
                HttpContext context) throws HttpException, IOException {
            System.out.println(request.getRequestLine());
            response.setStatusCode(HttpStatus.SC_OK);
            response.setEntity(
                    new StringEntity("message from request handler",
                            ContentType.TEXT_PLAIN));
        }
    };
    static HttpProcessor httpProcessor = HttpProcessorBuilder.create()
            .add(new ResponseDate())
            .add(new ResponseServer("Rest-in-Motion-HTTP/1.1"))
            .add(new ResponseContent())
            .add(new ResponseConnControl())
            // .add(new RequestExpectContinue(true))
            .build();

    public static void main(String[] args) throws IOException, InterruptedException, HttpException {
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

    public static void example2() throws IOException, InterruptedException {
        int port = 8080;
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();

        UriHttpRequestHandlerMapper handlerMapper = new UriHttpRequestHandlerMapper();
        handlerMapper.register("*", requestHandler);

        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .setServerInfo("Test/1.1")
                .setHttpProcessor(httpProcessor)
                .setSocketConfig(socketConfig)
                .setExceptionLogger(new StdErrorExceptionLogger())
                .setHandlerMapper(handlerMapper)
                .create();
        server.start();
        server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.shutdown(5, TimeUnit.SECONDS)));
    }

    static class StdErrorExceptionLogger implements ExceptionLogger {

        @Override
        public void log(final Exception ex) {
            if (ex instanceof SocketTimeoutException) {
                System.err.println("Connection timed out");
            } else if (ex instanceof ConnectionClosedException) {
                System.err.println(ex.getMessage());
            } else {
                ex.printStackTrace();
            }
        }

    }
}
