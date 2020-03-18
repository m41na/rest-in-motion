package works.hop.jetty.startup;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    @Test
    public void createFcgiHandler() {
        ServletContextHandler handler = AppFcgiHandler.createFcgiHandler(map);
        assertNotNull(handler);
        assertEquals("/", handler.getContextPath());
    }
}