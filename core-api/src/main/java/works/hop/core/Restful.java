package works.hop.core;

import works.hop.handler.HandlerFunction;
import works.hop.route.Routing;
import works.hop.traverse.Visitor;

import java.util.Map;

public interface Restful {

    String resolve(String path);

    Restful assets(String folder);

    Restful assets(String mapping, String folder);

    // ************* CORS Headers *****************//
    Restful cors(Map<String, String> cors);

    // ************* GET *****************//
    Restful get(String path, HandlerFunction handler);

    Restful get(String path, String accept, String type, HandlerFunction handler);

    Restful get(String path, String accept, String type, Map<String, String> headers, HandlerFunction handler);

    // ************* POST *****************//
    Restful post(String path, HandlerFunction handler);

    Restful post(String path, String accept, String type, HandlerFunction handler);

    Restful post(String path, String accept, String type, Map<String, String> headers, HandlerFunction handler);

    // ************* PUT *****************//
    Restful put(String path, HandlerFunction handler);

    Restful put(String path, String accept, String type, HandlerFunction handler);

    Restful put(String path, String accept, String type, Map<String, String> headers, HandlerFunction handler);

    // ************* DELETE *****************//
    Restful delete(String path, HandlerFunction handler);

    Restful delete(String path, String accept, String type, HandlerFunction handler);

    Restful delete(String path, String accept, String type, Map<String, String> headers, HandlerFunction handler);

    // ************* ALL *****************//
    Restful all(String path, HandlerFunction handler);

    Restful all(String path, String accept, String type, HandlerFunction handler);

    Restful all(String path, String accept, String type, Map<String, String> headers, HandlerFunction handler);

    // ************* GENERIC *****************//
    Restful route(String method, String path, HandlerFunction handler);

    Restful route(String method, String path, String accept, String type, HandlerFunction handler);

    Restful route(String method, String path, String accept, String type, Map<String, String> headers, HandlerFunction handler);

    // ************* SPECIALIZED HANDLERS *****************//
    Restful fcgi(String home, String proxyTo);

    // ************* ROUTES ************** //
    Routing.Router getRouter();

    void traverse(Visitor<Routing.Router, Routing.Route> visitor);
}
