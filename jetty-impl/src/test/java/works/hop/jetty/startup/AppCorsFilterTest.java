package works.hop.jetty.startup;

import org.eclipse.jetty.servlet.FilterHolder;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static works.hop.jetty.startup.AppOptions.UNASSIGNED;

public class AppCorsFilterTest {

    private AppCorsFilter corsFilter;
    private Map<String, String> map = new HashMap<>() {
        {
            put("assets", UNASSIGNED);
            put("https.outputBufferSize", "9018");
            put("https.idleTimeout", "30000");
            put("https.port", "8443");
            put("https.ssl.stsMaxAge", "120000");
        }
    };

    @Before
    public void setUp(){
        corsFilter = new AppCorsFilter();
    }

    @Test
    public void configCorsFilter() {
        FilterHolder filterHolder = corsFilter.configCorsFilter(map);
        assertNotNull(filterHolder);
        assertEquals("rest-api-cors-filter", filterHolder.getRegistration().getName());
    }
}