package works.hop.core;

import works.hop.route.Routing;

import java.util.Map;

public interface ARequest<REQ> {

    REQ request();

    String protocol();

    boolean secure();

    String hostname();

    String ip();

    String path();

    String param(String name);

    <T>T param(String name, Class<T> type);

    Map<String, String> pathParams();

    String query();

    String header(String name);

    <T> T attribute(String name, Class<T> type);

    boolean error();

    String message();

    long upload(String dest);

    long capture();

    byte[] body();

    <T> T body(Class<T> type);

    <T> T body(BodyReader<T> provider);

    Routing.Search route();

    void route(Routing.Search route);
}
