package works.hop.route;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static works.hop.core.RestMethods.INTERCEPT_ALL_URL;

public class MethodRouter implements Routing.Router {

    private Map<Method, Routing.Router> routers = new EnumMap<>(Method.class);

    public MethodRouter() {
        super();
        routers.put(Method.GET, new SplitPathRouter());
        routers.put(Method.PUT, new SplitPathRouter());
        routers.put(Method.POST, new SplitPathRouter());
        routers.put(Method.HEAD, new SplitPathRouter());
        routers.put(Method.DELETE, new SplitPathRouter());
        routers.put(Method.OPTIONS, new SplitPathRouter());
        routers.put(Method.ALL, new SplitPathRouter());
    }

    @Override
    public void search(Routing.Search criteria) {
        String method = criteria.attributes.method;
        Method type = method != null ? Method.valueOf(method.toUpperCase()) : null;
        if (type != null) {
            this.routers.get(type).search(criteria);
            //if a matching route is found, set the method value in the result
            if (criteria.route != null) {
                criteria.route.method = type.name();
                //now apply interceptors if they exist
                criteria.attributes.url = INTERCEPT_ALL_URL;
                criteria.attributes.method = Method.ALL.toString();
                this.routers.get(Method.ALL).search(criteria);
            }
        }
    }

    @Override
    public boolean contains(Routing.Search criteria) {
        return routers.containsKey(criteria.attributes.method) && routers.get(criteria.attributes.method).contains(criteria);
    }

    @Override
    public void info(List<String> nodes, String prefix) {
        String indent = prefix + "|-";
        routers.entrySet().stream().forEach(entry -> entry.getValue().info(nodes, (indent)));
    }

    @Override
    public void add(Routing.Route route) {
        String method = route.method;
        Method type = method != null ? Method.valueOf(method.toUpperCase()) : null;
        if (type != null) {
            this.routers.get(type).add(route);
        }
    }

    @Override
    public void remove(Routing.Route route) {
        if (routers.containsKey(route.method)) {
            routers.get(route.method).remove(route);
        }
    }

    public enum Method {POST, GET, PUT, DELETE, OPTIONS, HEAD, ALL}
}
