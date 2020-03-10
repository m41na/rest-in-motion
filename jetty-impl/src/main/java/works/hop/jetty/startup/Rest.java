package works.hop.jetty.startup;

import org.apache.commons.cli.Options;
import works.hop.jetty.JettyStartable;

import java.util.Map;
import java.util.function.Function;

import static works.hop.jetty.startup.AppOptions.applyDefaults;

public interface Rest {

    JettyStartable provide(Map<String, String> properties);

    Function<Map<String, String>, JettyStartable> build(JettyStartable server);

    default void start(String[] args) {
        start(new Options(), args);
    }

    default void start(Options options, String[] args) {
        Map<String, String> properties = applyDefaults(options, args);
        build(provide(properties)).apply(properties).listen(Integer.parseInt(properties.get("port")), properties.get("host"), (result) -> System.out.println(result));
    }
}
