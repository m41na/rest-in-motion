package works.hop.jetty;

import works.hop.route.Routing;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Vector;

public class MockRoute extends JettyRequest {

    private final Routing.Route route;

    public MockRoute(HttpServletRequest request, Routing.Route route) {
        super(request);
        this.route = route;
        if (route.accept != null && route.accept.trim().length() > 0) {
            this.route.headers.put("Accept", route.accept);
        }
        if (route.contentType != null && route.accept.trim().length() > 0) {
            this.route.headers.put("Content-Type", route.contentType);
        }
    }

    @Override
    public String getRequestURI() {
        return route.path;
    }

    @Override
    public String getMethod() {
        return route.method;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return new Vector<String>(route.headers.keySet()).elements();
    }

    @Override
    public String getHeader(String name) {
        if ("Accept".equalsIgnoreCase(name)) {
            return route.accept;
        }
        if ("Content-Type".equalsIgnoreCase(name)) {
            return route.contentType;
        }
        return route.headers.get(name);
    }
}
