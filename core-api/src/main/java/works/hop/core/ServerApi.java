package works.hop.core;

import works.hop.handler.Handler;
import works.hop.route.Routing;

import java.util.Map;

public abstract class ServerApi implements Rest {

    @Override
    public void all(String method, String url, String accept, String contentType, Map<String, String> headers, Handler handler) {
        addRoute(method, url, accept, contentType, headers, handler);
    }

    @Override
    public void get(String url, String accept, String contentType, Map<String, String> headers, Handler handler) {
        all("get", url, accept, contentType, headers, handler);
    }

    @Override
    public void post(String url, String accept, String contentType, Map<String, String> headers, Handler handler) {
        all("post", url, accept, contentType, headers, handler);
    }

    @Override
    public void put(String url, String accept, String contentType, Map<String, String> headers, Handler handler) {
        all("put", url, accept, contentType, headers, handler);
    }

    @Override
    public void delete(String url, String accept, String contentType, Map<String, String> headers, Handler handler) {
        all("delete", url, accept, contentType, headers, handler);
    }

    @Override
    public void addRoute(String method, String path, String accept, String contentType, Map<String, String> headers, Handler handler) {
        Routing.Route route = Routing.RouteBuilder.newRoute()
                .handler(handler)
                .accept(accept)
                .contentType(contentType)
                .path(path)
                .method(method)
                .headers(() -> headers)
                .build();
        getRouter().add(route);
    }
}
