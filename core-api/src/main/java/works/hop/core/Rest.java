package works.hop.core;

import org.apache.commons.cli.Options;

import java.util.Map;
import java.util.function.Function;

import static works.hop.cli.AppOptions.applyDefaults;

public interface Rest {

    Restful provide(Map<String, String> properties);

    Function<Map<String, String>, Restful> build(Restful server);

    default void start(String[] args) {
        start(new Options(), args);
    }

    default void start(Options options, String[] args) {
        Map<String, String> properties = applyDefaults(options, args);
        build(provide(properties)).apply(properties).listen(Integer.parseInt(properties.get("port")), properties.get("host"), (result) -> System.out.println(result));
    }
}
