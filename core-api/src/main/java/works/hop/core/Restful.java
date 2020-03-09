package works.hop.core;

import works.hop.handler.HandlerFunction;
import works.hop.route.Routing;

import java.util.Map;
import java.util.function.Consumer;

public interface Restful {

    String resolve(String path);

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

    // ************* SPECIALIZED *****************//

    // ************* Get Router instance ************** //
    Routing.Router getRouter();

    // ************* Start/Stop/Listen server ************** //
    void start() throws Exception;

    void shutdown() throws Exception;

    void listen(Integer port, String host);

    void listen(int port, String host, Consumer<String> result);
}
