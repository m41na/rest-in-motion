package works.hop.jetty.startup;

import org.eclipse.jetty.servlet.FilterHolder;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AppCorsFilterTest {

    private AppCorsFilter corsFilter;
    private Map<String, String> map = new HashMap<>() {
        {
            put("https.outputBufferSize", "9018");
            put("https.idleTimeout", "30000");
            put("https.port", "8443");
            put("https.ssl.stsMaxAge", "120000");
        }
    };

    @Test
    public void configCorsFilter() {
        FilterHolder filterHolder = AppCorsFilter.configCorsFilter(map);
        assertNotNull(filterHolder);
        assertEquals("rim-cors-filter", filterHolder.getRegistration().getName());
    }
}