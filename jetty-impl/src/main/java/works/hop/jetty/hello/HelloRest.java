package works.hop.jetty.hello;

import works.hop.jetty.JettyStartable;
import works.hop.jetty.startup.Rest;
import works.hop.jetty.websocket.JettyWsAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static works.hop.jetty.JettyStartable.createServer;

public class HelloRest {

    public static void main(String[] args) {
        Rest app = new Rest() {

            @Override
            public JettyStartable provide(Map<String, String> properties) {
                return createServer(properties);
            }

            @Override
            public Function<Map<String, String>, JettyStartable> build(JettyStartable server) {
                return props -> {
                    server.get("/api", "*", "", emptyMap(), (request, response, promise) -> {
                        response.ok(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date()));
                        promise.complete();
                    });
                    server.assets(System.getProperty("user.dir"));
                    server.websocket("/events/*", () -> new JettyWsAdapter());
                    return server;
                };
            }
        };
        app.start(args);
    }
}
