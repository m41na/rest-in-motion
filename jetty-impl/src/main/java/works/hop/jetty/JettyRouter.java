package works.hop.jetty;

import works.hop.route.Routing;

import java.util.Enumeration;
import java.util.List;

public class JettyRouter implements Routing.Router {

    private final Routing.Router routeTree;

    public JettyRouter(Routing.Router routeTree) {
        super();
        this.routeTree = routeTree;
    }

    public Routing.Search search(JettyRequest request) {
        return search(request.getRequestURI(), request);
    }

    public Routing.Search search(String target, JettyRequest request) {
        Routing.Attributes search = new Routing.Attributes();
        search.url = target;
        search.method = request.getMethod();
        for (Enumeration<String> keys = request.getHeaderNames(); keys.hasMoreElements(); ) {
            String key = keys.nextElement();
            search.headers.put(key, request.getHeader(key));
        }
        return searchRoute(search);
    }

    public Routing.Search searchRoute(Routing.Attributes requestAttrs) {
        Routing.Search input = new Routing.Search(requestAttrs);
        this.search(input);
        return input;
    }

    @Override
    public void search(Routing.Search criteria) {
        routeTree.search(criteria);
    }

    @Override
    public boolean contains(Routing.Search criteria) {
        return routeTree.contains(criteria);
    }

    @Override
    public void info(List<String> nodes, String prefix) {
        String indent = prefix + "|-";
        nodes.add(indent + "root");
        routeTree.info(nodes, "\n\t" + indent);
    }

    @Override
    public void add(Routing.Route route) {
        routeTree.add(route);
    }

    @Override
    public void remove(Routing.Route route) {
        routeTree.remove(route);
    }
}
