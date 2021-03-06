package works.hop.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import works.hop.handler.HandlerPromise;
import works.hop.handler.HandlerResult;
import works.hop.handler.impl.DefaultHandlerChain;
import works.hop.route.Routing;
import works.hop.traverse.Visitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class RestfulTest {

    private Restful server;
    @Mock
    private ARequest request;
    @Mock
    private AResponse response;

    private Map<String, String> props = new HashMap<>();
    private HandlerPromise promise;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        server = new BasicRestful((key) -> props.get(key));
        promise = new HandlerPromise();
        promise.OnSuccess(new Function<HandlerResult, HandlerResult>() {
            @Override
            public HandlerResult apply(HandlerResult handlerResult) {
                return handlerResult;
            }
        });
        promise.OnFailure(new BiFunction<HandlerResult, Throwable, HandlerResult>() {
            @Override
            public HandlerResult apply(HandlerResult handlerResult, Throwable throwable) {
                return handlerResult;
            }
        });
    }

    @Test
    public void getRequestOnContextPathShouldReturn200Ok() {
        server.get("/", "", "", Collections.emptyMap(),
                (request, response, done) -> done.resolve(() -> System.out.println("get request")));
        Routing.Search search = new Routing.Search(null);
        search.chain = new DefaultHandlerChain();
        server.getRouter().search(search);
        search.chain.intercept(search.route.handler, null, request, response, promise);
        HandlerResult result = promise.complete();
        assertTrue(result.success());
    }

    @Test
    public void postRequestOnContextPathShouldReturn200Ok() {
        server.before("post", "/", (request, response, done) -> {
            System.out.println("before post, do some magic!!");
        });
        server.post("/", "", "", Collections.emptyMap(),
                (request, response, done) -> done.resolve(() -> System.out.println("post request")));
        Routing.Search search = new Routing.Search(null);
        search.chain = new DefaultHandlerChain();
        server.getRouter().search(search);
        search.chain.intercept(search.route.handler, null, request, response, promise);
        HandlerResult result = promise.complete();
        assertTrue(result.success());
    }

    @Test
    public void putRequestOnContextPathShouldReturn200Ok() {
        server.put("/", "", "", Collections.emptyMap(),
                (request, response, done) -> done.resolve(() -> System.out.println("put request")));
        Routing.Search search = new Routing.Search(null);
        search.chain = new DefaultHandlerChain();
        server.getRouter().search(search);
        search.chain.intercept(search.route.handler, null, request, response, promise);
        HandlerResult result = promise.complete();
        assertTrue(result.success());
    }

    @Test
    public void deleteRequestOnContextPathShouldReturn200Ok() {
        server.delete("/", "", "", Collections.emptyMap(),
                (request, response, done) -> done.resolve(() -> System.out.println("delete request")));
        Routing.Search search = new Routing.Search(null);
        search.chain = new DefaultHandlerChain();
        server.getRouter().search(search);
        search.chain.intercept(search.route.handler, null, request, response, promise);
        HandlerResult result = promise.complete();
        assertTrue(result.success());
    }

    class BasicRestful implements Restful {

        BasicRouter router = new BasicRouter();
        Function<String, String> properties;

        public BasicRestful(Function<String, String> properties) {
            this.properties = properties;
        }

        @Override
        public Restful assets(String folder) {
            return null;
        }

        @Override
        public Restful assets(String mapping, String folder) {
            return null;
        }

        @Override
        public Function<String, String> properties() {
            return properties;
        }

        @Override
        public Restful mount(String path) {
            return null;
        }

        @Override
        public Restful session(Function<String, String> properties) {
            return null;
        }

        @Override
        public Restful fcgi(String context, String home, String proxyTo) {
            return null;
        }

        @Override
        public void traverse(Visitor<Routing.Router> visitor) {
        }

        @Override
        public Routing.Router getRouter() {
            return router;
        }

        @Override
        public String getContext() {
            return "/";
        }
    }

    class BasicRouter implements Routing.Router {

        private Routing.Route route;

        @Override
        public void search(Routing.Search criteria) {
            criteria.route = route;
        }

        @Override
        public boolean contains(Routing.Search criteria) {
            return false;
        }

        @Override
        public void info(List<String> nodes, String prefix) {
        }

        @Override
        public void add(Routing.Route route) {
            this.route = route;
        }

        @Override
        public void remove(Routing.Route route) {
        }
    }
}
