package works.hop.core;

import works.hop.handler.Handler;
import works.hop.route.Routing;

import java.util.Map;

public interface Rest {

    void all(String method, String url, String accept, String contentType, Map<String, String> headers, Handler handler);

    void get(String url, String accept, String contentType, Map<String, String> headers, Handler handler);

    void post(String url, String accept, String contentType, Map<String, String> headers, Handler handler);

    void put(String url, String accept, String contentType, Map<String, String> headers, Handler handler);

    void delete(String url, String accept, String contentType, Map<String, String> headers, Handler handler);

    void addRoute(String method, String path, String accept, String contentType, Map<String, String> headers, Handler handler);

    Routing.Router getRouter();

    void start() throws Exception;

    void shutdown() throws Exception;

    void listen(Integer port, String host) throws Exception;
}
