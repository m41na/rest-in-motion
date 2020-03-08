package works.hop.core;

import works.hop.handler.HandlerFunction;
import works.hop.route.Routing;

import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public abstract class RestServer implements Restful {

    public static final String APP_CTX_KEY = "appctx";
    protected final Function<String, String> properties;

    public RestServer(Function<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String resolve(String path) {
        String appctx = properties.apply(APP_CTX_KEY);
        String path1 = !appctx.startsWith("/") ? "/" + appctx : appctx;
        if (path1.endsWith("/")) {
            path1 = path1.substring(0, path1.length() - 1);
        }
        String path2 = !path.startsWith("/") ? "/" + path : path;
        return path1 + path2;
    }

    @Override
    public Restful get(String path, HandlerFunction handler) {
        return route("get", path, "*", "*", handler);
    }

    @Override
    public Restful get(String path, String accept, String type, HandlerFunction handler) {
        return route("get", path, accept, type, emptyMap(), handler);
    }

    @Override
    public Restful get(String url, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        return route("get", url, accept, type, headers, handler);
    }

    @Override
    public Restful post(String path, HandlerFunction handler) {
        return route("post", path, "*", "*", handler);
    }

    @Override
    public Restful post(String path, String accept, String type, HandlerFunction handler) {
        return route("post", path, accept, type, emptyMap(), handler);
    }

    @Override
    public Restful post(String url, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        return route("post", url, accept, type, headers, handler);
    }

    @Override
    public Restful put(String path, HandlerFunction handler) {
        return route("put", path, "*", "*", handler);
    }

    @Override
    public Restful put(String path, String accept, String type, HandlerFunction handler) {
        return route("put", path, accept, type, emptyMap(), handler);
    }

    @Override
    public Restful put(String path, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        return route("put", path, accept, type, headers, handler);
    }

    @Override
    public Restful delete(String path, HandlerFunction handler) {
        return route("delete", path, "*", "*", handler);
    }

    @Override
    public Restful delete(String path, String accept, String type, HandlerFunction handler) {
        return route("delete", path, accept, type, emptyMap(), handler);
    }

    @Override
    public Restful delete(String url, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        return route("delete", url, accept, type, headers, handler);
    }

    @Override
    public Restful all(String path, HandlerFunction handler) {
        return route("all", path, "*", "*", handler);
    }

    @Override
    public Restful all(String path, String accept, String type, HandlerFunction handler) {
        return route("all", path, accept, type, emptyMap(), handler);
    }

    @Override
    public Restful all(String url, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        return route("all", url, accept, type, headers, handler);
    }

    @Override
    public Restful route(String method, String path, HandlerFunction handler) {
        return route(method, path, "*", "*", handler);
    }

    @Override
    public Restful route(String method, String path, String accept, String type, HandlerFunction handler) {
        return route(method, path, accept, type, emptyMap(), handler);
    }

    @Override
    public RestServer route(String method, String path, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        Routing.Route route = Routing.RouteBuilder.newRoute()
                .handler(handler)
                .accept(accept)
                .contentType(type)
                .path(resolve(path))
                .method(method)
                .headers(() -> headers)
                .build();
        getRouter().add(route);
        return this;
    }
}
