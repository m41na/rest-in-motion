package works.hop.core;

import works.hop.handler.HandlerFunction;
import works.hop.route.Routing;

import java.util.Map;

import static java.util.Collections.emptyMap;

public interface RestMethods {

    // ************* GET *****************//
    default RestMethods get(String path, HandlerFunction handler) {
        return route("get", path, "*", "*", handler);
    }

    default RestMethods get(String path, String accept, String type, HandlerFunction handler) {
        return route("get", path, accept, type, emptyMap(), handler);
    }

    default RestMethods get(String url, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        return route("get", url, accept, type, headers, handler);
    }

    // ************* POST *****************//
    default RestMethods post(String path, HandlerFunction handler) {
        return route("post", path, "*", "*", handler);
    }

    default RestMethods post(String path, String accept, String type, HandlerFunction handler) {
        return route("post", path, accept, type, emptyMap(), handler);
    }

    default RestMethods post(String url, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        return route("post", url, accept, type, headers, handler);
    }

    // ************* PUT *****************//
    default RestMethods put(String path, HandlerFunction handler) {
        return route("put", path, "*", "*", handler);
    }

    default RestMethods put(String path, String accept, String type, HandlerFunction handler) {
        return route("put", path, accept, type, emptyMap(), handler);
    }

    default RestMethods put(String path, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        return route("put", path, accept, type, headers, handler);
    }

    // ************* DELETE *****************//
    default RestMethods delete(String path, HandlerFunction handler) {
        return route("delete", path, "*", "*", handler);
    }

    default RestMethods delete(String path, String accept, String type, HandlerFunction handler) {
        return route("delete", path, accept, type, emptyMap(), handler);
    }

    default RestMethods delete(String url, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        return route("delete", url, accept, type, headers, handler);
    }

    // ************* ALL *****************//
    default RestMethods all(String path, HandlerFunction handler) {
        return route("all", path, "*", "*", handler);
    }

    default RestMethods all(String path, String accept, String type, HandlerFunction handler) {
        return route("all", path, accept, type, emptyMap(), handler);
    }

    default RestMethods all(String url, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        return route("all", url, accept, type, headers, handler);
    }

    // ************* GENERIC *****************//
    default RestMethods route(String method, String path, HandlerFunction handler) {
        return route(method, path, "*", "*", handler);
    }

    default RestMethods route(String method, String path, String accept, String type, HandlerFunction handler) {
        return route(method, path, accept, type, emptyMap(), handler);
    }

    default RestMethods route(String method, String path, String accept, String type, Map<String, String> headers, HandlerFunction handler) {
        Routing.Route route = Routing.RouteBuilder.newRoute()
                .handler(handler)
                .accept(accept)
                .contentType(type)
                .path(resolve(path, getContext()))
                .method(method)
                .headers(() -> headers)
                .build();
        getRouter().add(route);
        return this;
    }

    // ************* BEFORE/AFTER *****************//
    default RestMethods before(String method, String path, HandlerFunction handler) {
        return route("before:" + method, path, handler);
    }

    default RestMethods after(String method, String path, HandlerFunction handler) {
        return route("after:" + method, path, handler);
    }

    // ************* Static Resources *****************//
    RestMethods assets(String folder);

    RestMethods assets(String mapping, String folder);

    // ************* ROUTES ************** //
    Routing.Router getRouter();

    String getContext();

    // ************* UTILITIES ************** //

    default String resolve(String path, String context) {
        String path1 = !context.startsWith("/") ? "/" + context : context;
        if (path1.endsWith("/")) {
            path1 = path1.substring(0, path1.length() - 1);
        }
        String path2 = !path.startsWith("/") ? "/" + path : path;
        return path1 + path2;
    }
}