package works.hop.jetty.startup;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static works.hop.jetty.startup.AppOptions.UNASSIGNED;

public class AppFcgiHandlerTest {

    private AppFcgiHandler fcgiHandler;
    private Map<String, String> map = new HashMap<>() {
        {
            put("contextPath", "/");
            put("resourceBase", "www");
            put("welcomeFile", "index.php");
            put("assetsContext", "/");
            put("init.files", "$path /index.php?p=$path");
            put("init.dirAllowed", "false");
            put("init.proxyTo", "localhost:8081/api");
            put("init.prefix", "/");
            put("init.scriptRoot", "cgi");
            put("init.scriptPattern", "(.+?\\\\.php)");
        }
    };

    @Before
    public void setUp(){
        fcgiHandler = new AppFcgiHandler();
    }

    @Test
    public void createFcgiHandler() {
        ServletContextHandler handler = fcgiHandler.createFcgiHandler(map);
        assertNotNull(handler);
        assertEquals("/", handler.getContextPath());
    }
}