package works.hop.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class InterceptRouter implements Routing.Router {

    private final Logger LOGGER = LoggerFactory.getLogger(InterceptRouter.class);
    private Map<Routing.RouteType, Vector<Routing.Route>> interceptors = new EnumMap<>(Routing.RouteType.class);

    public InterceptRouter() {
        super();
        interceptors.put(Routing.RouteType.AFTER, new Vector<>());
        interceptors.put(Routing.RouteType.BEFORE, new Vector<>());
    }

    @Override
    public void search(Routing.Search criteria) {
        //add pre-handlers
        for (Routing.Route route : interceptors.get(Routing.RouteType.BEFORE)) {
            criteria.chain.addFirst(route.handler);
        }
        //add handler
        criteria.chain.addLast(criteria.route.handler);
        //add post-handlers
        for (Routing.Route route : interceptors.get(Routing.RouteType.AFTER)) {
            criteria.chain.addLast(route.handler);
        }
    }

    @Override
    public boolean contains(Routing.Search criteria) {
        return true;
    }

    @Override
    public void info(List<String> nodes, String prefix) {
        String indent = prefix + "|-";
        interceptors.entrySet().stream().forEach(entry -> {
            String vector = interceptors.get(entry.getKey()).stream().map(route -> route.toString()).reduce("", (x, y) -> x + ",\t" + y);
            nodes.add(indent + entry.getValue().toString().concat(vector));
        });
    }

    @Override
    public void add(Routing.Route route) {
        switch (route.type) {
            case BEFORE:
                this.interceptors.get(Routing.RouteType.BEFORE).add(route);
                break;
            case AFTER:
                this.interceptors.get(Routing.RouteType.AFTER).add(route);
                break;
            default:
                LOGGER.error("NOT EXPECTING handler to be reached from an InterceptRouter node");
                break;
        }
    }

    @Override
    public void remove(Routing.Route route) {
        LOGGER.error("Currently, you cannot remove a handler that's already configured");
    }
}
