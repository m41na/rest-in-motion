package works.hop.jetty;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import works.hop.handler.HandlerFunction;
import works.hop.route.MethodRouter;
import works.hop.route.Routing;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class JettyRouterTest {

    private JettyRouter router;
    @Mock
    private HandlerFunction handler;
    @Mock
    private HttpServletRequest servletRequest;

    public JettyRouterTest() {
        router = new JettyRouter(new MethodRouter());
        //add todo routes
        router.add(Routing.RouteBuilder.create(handler, "/todos", "get", null, "application/json"));
        router.add(Routing.RouteBuilder.create(handler, Routing.RouteType.BEFORE, "/todos", "get"));
        router.add(Routing.RouteBuilder.create(handler, "/todos", "post", "application/x-www-form-urlencoded", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, Routing.RouteType.BEFORE, "/todos", "post", "application/x-www-form-urlencoded", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, Routing.RouteType.AFTER, "/todos", "post", "application/x-www-form-urlencoded", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/todos/{id}", "get", "application/json", null));
        router.add(Routing.RouteBuilder.create(handler, "/todos/{id}/status", "get", "application/json", null));
        router.add(Routing.RouteBuilder.create(handler, "/todos/{id}/name", "put", "application/x-www-form-urlencoded", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, Routing.RouteType.AFTER, "/todos/{id}/name", "put"));
        router.add(Routing.RouteBuilder.create(handler, "/todos/{id}/done", "put", "application/x-www-form-urlencoded", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/todos/{id}", "delete"));
        //add blog routes
        router.add(Routing.RouteBuilder.create(handler, "/blog", "post", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/blog/{bid}/author", "post", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/blog/{bid}", "get", "application/json", null));
        router.add(Routing.RouteBuilder.create(handler, "/blog/{bid}/author/{uid}", "get", "application/json", null));
        router.add(Routing.RouteBuilder.create(handler, "/blog/{bid}", "put", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/blog/{bid}/author/{uid}", "put", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/blog/{bid}", "delete", "application/json", null));
        router.add(Routing.RouteBuilder.create(handler, "/blog/{bid}/author/{uid}", "delete", "application/json", null));
        //add book routes
        router.add(Routing.RouteBuilder.create(handler, "/book", "post", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}", "get", "application/json", ""));
        router.add(Routing.RouteBuilder.create(handler, "/book", "get", "application/json", ""));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}", "put", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}", "delete", "application/json", ""));

        router.add(Routing.RouteBuilder.create(handler, "/book", "get", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}", "get", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author", "get", "application/json", ""));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author/{uid}", "get", "application/json", ""));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author/{uid}/name", "get", "application/json", ""));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author/{uid}/address/{city}", "get", "application/json", ""));

        router.add(Routing.RouteBuilder.create(handler, "/book/new", "post", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}", "post", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author", "post", "application/json", ""));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author/{uid}", "post", "application/json", ""));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author/{uid}/name", "post", "application/json", ""));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author/{uid}/address/{city}", "post", "application/json", ""));

        router.add(Routing.RouteBuilder.create(handler, "/book", "put", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}", "put", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author", "put", "application/json", ""));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author/{uid}", "put", "application/json", ""));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author/{uid}/name", "put", "application/json", ""));
        router.add(Routing.RouteBuilder.create(handler, "/book/{id}/author/{uid}/address/{city}", "put", "application/json", ""));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSearchRoute() {
        Routing.Attributes getTodo = new Routing.Attributes();
        getTodo.method = "get";
        getTodo.url = "/todos/20";
        getTodo.headers.put("Accept", "application/json");
        //search
        Routing.Search getTodoFound = router.searchRoute(getTodo);
        assertNotNull("Expecting result found", getTodoFound.chain);
        assertEquals("Expecting 1 param", 1, getTodoFound.pathParams.size());

        Routing.Attributes getStatus = new Routing.Attributes();
        getStatus.method = "get";
        getStatus.url = "/todos/20/status";
        getStatus.headers.put("Accept", "application/json");
        //search
        Routing.Search getStatusFound = router.searchRoute(getStatus);
        assertNotNull("Expecting result found", getStatusFound.chain);
        assertEquals("Expecting 1 param", 1, getStatusFound.pathParams.size());

        Routing.Attributes getAuthor = new Routing.Attributes();
        getAuthor.method = "get";
        getAuthor.url = "/blog/20/author/4";
        getAuthor.headers.put("Accept", "application/json");
        //search
        Routing.Search getAuthorFound = router.searchRoute(getAuthor);
        assertNotNull("Expecting result found", getAuthorFound.chain);
        assertEquals("Expecting 2 param", 2, getAuthorFound.pathParams.size());

        Routing.Attributes delAuthor = new Routing.Attributes();
        delAuthor.method = "delete";
        delAuthor.url = "/blog/10/author/6";
        delAuthor.headers.put("Accept", "application/json");
        //search
        Routing.Search delAuthorFound = router.searchRoute(delAuthor);
        assertNotNull("Expecting result found", delAuthorFound.chain);
        assertEquals("Expecting 2 param", 2, delAuthorFound.pathParams.size());
    }

    @Test
    public void testMatchBookAuthorNameRoute() {
        Routing.Route incoming = new Routing.Route(handler, "/book/1234/author/6789/name", "get", "application/json", null);
        JettyRequest request = new MockRoute(servletRequest, incoming);
        Routing.Search match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting '1234'", "1234", match.pathParams.get("id"));
        assertEquals("Expecting '6789'", "6789", match.pathParams.get("uid"));
    }

    @Test
    public void testMatchBookAuthorCityRoute() {
        Routing.Route incoming = new Routing.Route(handler, "/book/9876/author/5432/address/chicago", "get", "application/json", "");
        JettyRequest request = new MockRoute(servletRequest, incoming);
        Routing.Search match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting '9876'", "9876", match.pathParams.get("id"));
        assertEquals("Expecting '5432'", "5432", match.pathParams.get("uid"));
        assertEquals("Expecting 'chicago'", "chicago", match.pathParams.get("city"));
    }

    @Test
    public void testMatchBookPathWithDifferentMethods() {
        Routing.Route incoming = new Routing.Route(handler, "/book", "get", "application/json", "application/json");
        JettyRequest request = new MockRoute(servletRequest, incoming);
        Routing.Search match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting 'get'", "get", match.route.method.toLowerCase());

        incoming = new Routing.Route(handler, "/book", "post", "application/json", "application/json");
        request = new MockRoute(servletRequest, incoming);
        match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting 'post'", "post", match.route.method.toLowerCase());

        incoming = new Routing.Route(handler, "/book", "put", "application/json", "application/json");
        request = new MockRoute(servletRequest, incoming);
        match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting 'put'", "put", match.route.method.toLowerCase());
    }

    @Test
    public void testRegexPathMatching() {
        String input = "/book/9876/author/5432/address/chicago";
        String route = "/book/{id}/author/{uid}/address/{city}";
        Map<String, String> params = new HashMap<>();

        //step 1. identify param patterns in the configured route (\\{.+?\\})
        //step 2. replace the patterns identified with literals (:.*?)
        //step 3. resulting string from step 3 becomes regex dest match agains input uri
        String paramRegex = "(\\{.+?\\})";
        String inputRegex = route.replaceAll(paramRegex, "\\\\{(.+?)\\\\}").replaceAll("\\/", "\\\\/");
        Pattern inputPattern = Pattern.compile("^" + inputRegex + "$");

        //step 4. match identified params from route
        Pattern paramPattern = Pattern.compile(paramRegex);
        Matcher paramMatcher = paramPattern.matcher(route);

        System.out.printf("derived route pattern [%s] from route [%s] dest match against input [%s]%n", inputPattern.pattern(), route, input);

        Matcher inputMatcher = inputPattern.matcher(input);
        if (inputMatcher.matches()) {
            int index = 1;
            while (paramMatcher.find()) {
                String param = paramMatcher.group(1);
                String value = inputMatcher.group(index++);
                params.put(param, value);
            }
            System.out.printf("the input [%s] matched the pattern [%s]", input, inputPattern.pattern());
        }
        System.out.println(params);
    }

    @Test
    public void testDisplayRoutesHierarchy() {
        List<String> info = new LinkedList<>();
        router.info(info, "");
        System.out.println(info);
    }
}
