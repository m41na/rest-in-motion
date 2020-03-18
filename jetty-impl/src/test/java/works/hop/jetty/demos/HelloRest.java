package works.hop.jetty.demos;

import org.apache.commons.cli.Options;
import works.hop.jetty.JettyStartable;
import works.hop.jetty.startup.Rest;
import works.hop.jetty.websocket.JettyWsEcho;
import works.hop.jetty.websocket.JettyWsPolicy;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static works.hop.jetty.JettyStartable.createServer;
import static works.hop.jetty.startup.AppOptions.applyDefaults;

public class HelloRest {

    public static Consumer<String[]> Rest_Hello_BareBones = args -> {
        Map<String, String> properties = applyDefaults(new Options(), args);
        var app = createServer(properties);
        app.context(properties.get("appctx"));
        app.get("/", (auth, req, res, done) -> done.resolve(() -> {
            res.ok("hello world");
            return null;
        }));
        app.listen(8090, "localhost");
    };

    public static Consumer<String[]> Rest_1_AssetsFolder = args -> {
        Rest app = new Rest() {

            @Override
            public JettyStartable provide(Map<String, String> properties) {
                var server = createServer(properties);
                server.context(properties.get("appctx")).assets(Paths.get(System.getProperty("user.dir"), "src/test/resources/dist").toString());
                return server;
            }

            @Override
            public Function<Map<String, String>, JettyStartable> build(JettyStartable server) {
                return props -> {
                    server.get("/", (auth, request, response, promise) -> {
                        response.ok(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date()));
                        promise.complete();
                    });
                    server.get("/2", "*", "", emptyMap(), (auth, request, response, promise) -> {
                        response.ok(new SimpleDateFormat("HH:mm:ss z").format(new Date()));
                        promise.complete();
                    });
                    return server;
                };
            }
        };
        app.start(args);
    };

    public static Consumer<String[]> Rest_1_AssetsFolder_EchoWebSocket = args -> {
        Rest app = new Rest() {

            @Override
            public JettyStartable provide(Map<String, String> properties) {
                var server = createServer(properties);
                server.rest().context(properties.get("appctx"), builder ->
                        builder.websocket("/echo/*", () -> new JettyWsEcho(), JettyWsPolicy.defaultPolicy()).build()
                );
                return server;
            }

            @Override
            public Function<Map<String, String>, JettyStartable> build(JettyStartable server) {
                return props -> {
                    server.get("/3", "*", "", emptyMap(), (auth, request, response, promise) -> {
                        response.ok(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date()));
                        promise.complete();
                    });
                    server.assets(Paths.get(System.getProperty("user.dir"), "src/test/resources/dist").toString());
                    return server;
                };
            }
        };
        app.start(args);
    };

    public static Consumer<String[]> Rest_2_AssetsFolder_Multiple_Contexts_EchoWebSocket_Fcgi = args -> {
        Rest app = new Rest() {

            @Override
            public JettyStartable provide(Map<String, String> properties) {
                JettyStartable server = createServer(properties);
                server.context(properties.get("appctx"));
                server.rest().context("/ws", builder -> builder.websocket("/echo/*", () -> new JettyWsEcho(), JettyWsPolicy.defaultPolicy()).build());
                return server;
            }

            @Override
            public Function<Map<String, String>, JettyStartable> build(JettyStartable server) {
                return props -> {
                    server.get("/4", (auth, req, res, done) -> {
                        res.ok(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date()));
                        done.complete();
                    });
                    server.get("/5/", (auth, req, res, done) -> done.resolve(() -> {
                        res.ok("Yes from servlet");
                        return true;
                    }));
                    server.assets(Paths.get(System.getProperty("user.dir"), "src/test/resources/dist").toString());
                    server.assets("/dist", Paths.get(System.getProperty("user.dir"), "src/test/resources/dist2").toString());
                    server.fcgi("/wp", "/home/mainas/projects", "http://localhost:9000");
                    return server;
                };
            }
        };
        app.start(args);
    };

    public static Consumer<String[]> Rest_2_AssetsFolder_Multiple_Contexts_EchoWebSocket_Wordpress = args -> {
        Rest app = new Rest() {

            @Override
            public JettyStartable provide(Map<String, String> properties) {
                //properties.put(APP_CTX_KEY, "/api/");
                return createServer(properties);
            }

            @Override
            public Function<Map<String, String>, JettyStartable> build(JettyStartable server) {
                return props -> {
                    server.get("/", "*", "*", (auth, req, res, done) -> {
                        res.ok(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date()));
                        done.complete();
                    });
                    server.get("/6/", (auth, req, res, done) -> done.resolve(() -> {
                        res.ok("Yes from servlet");
                        return true;
                    }));
                    server.assets("/dist", Paths.get(System.getProperty("user.dir"), "src/test/resources/dist2").toString());
                    server.rest().websocket("/echo/*", () -> new JettyWsEcho(), JettyWsPolicy.defaultPolicy());
                    server.fcgi("/", "/home/mainas/projects/wordpress", "http://localhost:9000");
                    return server;
                };
            }
        };
        app.start(args);
    };

    public static void main(String[] args) {
        Map<Integer, Consumer> targets = new HashMap<>();
        targets.put(1, Rest_Hello_BareBones);
        targets.put(2, Rest_1_AssetsFolder);
        targets.put(3, Rest_1_AssetsFolder_EchoWebSocket);
        targets.put(4, Rest_2_AssetsFolder_Multiple_Contexts_EchoWebSocket_Fcgi);
        targets.put(5, Rest_2_AssetsFolder_Multiple_Contexts_EchoWebSocket_Wordpress);

        //execute from map
        targets.get(3).accept(args);
    }
}
