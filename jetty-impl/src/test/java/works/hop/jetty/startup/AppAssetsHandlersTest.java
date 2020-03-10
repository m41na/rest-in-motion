package works.hop.jetty.startup;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;

public class AppAssetsHandlersTest {

    private AppAssetsHandlers appAssets;
    private Map<String, String> map = new HashMap<>() {
        {
            put("assets.welcomeFile", "home.html");
            put("assets.pathInfoOnly", "false");
            put("assets.dirAllowed", "true");
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
        ServletHolder holder = appAssets.createResourceServlet(properties, "/file");
        assertNotNull(holder);
        assertEquals("home.html", holder.getInitParameter("welcomeFile"));
        assertEquals("false", holder.getInitParameter("pathInfoOnly"));
        assertEquals("true", holder.getInitParameter("dirAllowed"));
    }

    @Test
    public void configureAssetsToUseContextHandler() {
        ResourceHandler handler = appAssets.createResourceHandler(properties, "");
        assertNotNull(handler);
        assertThat(Arrays.asList(handler.getWelcomeFiles()), hasSize(1));
        assertThat(Arrays.asList(handler.getWelcomeFiles()), Matchers.contains(("home.html")));
        assertTrue(handler.isDirAllowed());
        assertFalse(handler.isPathInfoOnly());
    }
}