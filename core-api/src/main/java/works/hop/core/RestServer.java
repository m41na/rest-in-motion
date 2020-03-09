package works.hop.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.handler.HandlerFunction;
import works.hop.route.Routing;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public abstract class RestServer implements Restful {

    public static final Logger LOG = LoggerFactory.getLogger(RestServer.class);

    public static final String APP_CTX_KEY = "appctx";
    protected final Function<String, String> properties;
    private String status = "stopped";

    public RestServer(Function<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public Restful rest() {
        return this;
    }

    @Override
    public String status() {
        return this.status;
    }

    @Override
    public void banner() {
        try (InputStream is = getClass().getResourceAsStream(properties.apply("splash"))) {
            if (is != null) {
                int maxSize = 1024;
                byte[] bytes = new byte[maxSize];
                int size = is.read(bytes);
                System.out.printf("splash file is %d bytes in size of a max acceptable %d bytes%n", size, maxSize);
                LOG.info(new String(bytes, 0, size));
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
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
