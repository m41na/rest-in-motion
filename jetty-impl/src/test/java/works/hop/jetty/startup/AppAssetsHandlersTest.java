package works.hop.jetty.startup;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static works.hop.jetty.startup.AppOptions.UNASSIGNED;

public class AppAssetsHandlersTest {

    private AppAssetsHandlers appAssets;
    private String USE_SERVLET_RESOURCES = "assets.default.servlet";
    private Map<String, String> map = new HashMap<>() {
        {
            put("assets", UNASSIGNED);
        }
    };
    private Function<String, String> properties = s -> map.getOrDefault(s, s);

    @Mock
    private Collection<ContextHandler> contexts;
    @Mock
    private ServletContextHandler servlets;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        appAssets = new AppAssetsHandlers();
        doAnswer(invocation -> {
            System.out.println("ContextHandler will be used for resources");
            return null;
        }).when(contexts).add(any(ContextHandler.class));
        doAnswer(invocation -> {
            System.out.println("ServletHandler will be used for resources");
            return null;
        }).when(servlets).addServlet(any(ServletHolder.class), anyString());
    }

    @Test
    public void configureAssetsToUseServletHandler() {
        map.put(USE_SERVLET_RESOURCES, "true");
        appAssets.configureAssets(properties, contexts, servlets, "/", "dir");
        verify(servlets, times(1));
    }

    @Test
    @Ignore("figure out what's making the test fail when ran from the class level")
    public void configureAssetsToUseContextHandler() {
        map.put(USE_SERVLET_RESOURCES, "false");
        appAssets.configureAssets(properties, contexts, servlets, "/", "dir");
        verify(contexts, times(1));
    }
}