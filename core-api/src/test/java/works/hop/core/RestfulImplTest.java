package works.hop.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import works.hop.handler.HandlerPromise;
import works.hop.handler.HandlerResult;
import works.hop.route.Routing;
import works.hop.traverse.Visitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;
import static works.hop.core.RestfulImpl.APP_CTX_KEY;

public class RestfulImplTest {

    private RestfulImpl server;
    @Mock
    private ARequest request;
    @Mock
    private AResponse response;

    private Map<String, String> props = new HashMap<>();
    private HandlerPromise promise;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        props.put(APP_CTX_KEY, "/");
        server = new BasicRestfulImpl((key) -> props.get(key));
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
                (request, response, done) -> done.resolve(CompletableFuture.completedFuture("get request")));
        Routing.Search search = new Routing.Search(null);
        server.getRouter().search(search);
        search.result.handler.handle(request, response, promise);
        HandlerResult result = promise.complete();
        assertTrue(result.isSuccess());
    }

    @Test
    public void postRequestOnContextPathShouldReturn200Ok() {
        server.post("/", "", "", Collections.emptyMap(),
                (request, response, done) -> done.resolve(CompletableFuture.completedFuture("post request")));
        Routing.Search search = new Routing.Search(null);
        server.getRouter().search(search);
        search.result.handler.handle(request, response, promise);
        HandlerResult result = promise.complete();
        assertTrue(result.isSuccess());
    }

    @Test
    public void putRequestOnContextPathShouldReturn200Ok() {
        server.put("/", "", "", Collections.emptyMap(),
                (request, response, done) -> done.resolve(CompletableFuture.completedFuture("put request")));
        Routing.Search search = new Routing.Search(null);
        server.getRouter().search(search);
        search.result.handler.handle(request, response, promise);
        HandlerResult result = promise.complete();
        assertTrue(result.isSuccess());
    }

    @Test
    public void deleteRequestOnContextPathShouldReturn200Ok() {
        server.delete("/", "", "", Collections.emptyMap(),
                (request, response, done) -> done.resolve(CompletableFuture.completedFuture("delete request")));
        Routing.Search search = new Routing.Search(null);
        server.getRouter().search(search);
        search.result.handler.handle(request, response, promise);
        HandlerResult result = promise.complete();
        assertTrue(result.isSuccess());
    }

    class BasicRestfulImpl extends RestfulImpl {

        BasicRouter router = new BasicRouter();

        public BasicRestfulImpl(Function<String, String> properties) {
            super(properties);
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
        public Restful cors(Map<String, String> cors) {
            return null;
        }

        @Override
        public Restful fcgi(String home, String proxyTo) {
            return null;
        }

        @Override
        public Routing.Router getRouter() {
            return router;
        }

        @Override
        public void traverse(Visitor<Routing.Router, Routing.Route> visitor) {
        }
    }

    class BasicRouter implements Routing.Router {

        private Routing.Route route;

        @Override
        public void search(Routing.Search criteria) {
            criteria.result = route;
        }

        @Override
        public boolean contains(Routing.Search criteria) {
            return false;
        }

        @Override
        public void info(List<String> nodes, String prefix) {
        }

        @Override
        public void add(Routing.Route entity) {
            this.route = entity;
        }

        @Override
        public void remove(Routing.Route entity) {
        }
    }
}
