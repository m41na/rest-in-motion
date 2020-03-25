package works.hop.core;

import works.hop.route.Routing;
import works.hop.traverse.Visitor;

import java.util.function.Function;

public interface Restful extends RestMethods {

    Function<String, String> properties();

    Restful mount(String path);

    Restful session(Function<String, String> properties);

    // ************* SPECIALIZED HANDLERS *****************//
    Restful fcgi(String context, String home, String proxyTo);

    void traverse(Visitor<Routing.Router> visitor);
}
