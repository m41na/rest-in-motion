package works.hop.core;

import works.hop.route.Routing;

import java.util.Map;

public interface ARequest {

    void initialize();

    String protocol();

    boolean secure();

    String hostname();

    String ip();

    String path();

    String param(String name); //char, short, int, long, float, double, boolean, string

    Short shortParam(String name);

    Integer intParam(String name);

    Long longParam(String name);

    Float floatParam(String name);

    Double doubleParam(String name);

    Boolean boolParam(String name);

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

    String requestLine();

    String method();
}
