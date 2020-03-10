package works.hop.jetty.startup;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.*;

public class AppConnectorsTest {

    private AppConnectors connectors;
    private Map<String, String> map = new HashMap<>() {
        {
            put("https.outputBufferSize", "9018");
            put("https.idleTimeout", "30000");
            put("https.port", "8443");
            put("https.ssl.stsMaxAge", "120000");
        }
    };
    private Function<String, String> properties = s -> map.getOrDefault(s, s);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        connectors = new AppConnectors();
    }

    @Test
    public void createHttpConfiguration() {
        HttpConfiguration http = connectors.createHttpConfiguration(properties);
        assertNotNull(http);
        assertEquals(8443, http.getSecurePort());
        assertEquals(30000, http.getIdleTimeout());
        assertEquals("https", http.getSecureScheme());
        assertEquals(9018, http.getOutputBufferSize());
    }

    @Test
    public void configureHttpConnector() {
        HttpConfiguration http = connectors.createHttpConfiguration(properties);
        ServerConnector connector = connectors.configureHttpConnector(properties, new Server(), "localhost", 8080, http);
        assertNotNull(connector);
        assertEquals(8080, connector.getPort());
        assertEquals("localhost", connector.getHost());
        assertEquals(1, connector.getProtocols().size());
    }

    @Test
    public void configureHttpsConnector() {
        HttpConfiguration http = connectors.createHttpConfiguration(properties);
        ServerConnector connector = connectors.configureHttpsConnector(properties, new Server(), "localhost", 8080, http);
        assertNotNull(connector);
        assertEquals(8443, connector.getPort());
        assertTrue(connector.getProtocols().containsAll(Arrays.asList("ssl", "http/1.1", "h2", "alpn")));
    }

    @Test
    public void createHttpsCustomizer() {
        SecureRequestCustomizer src = connectors.createHttpsCustomizer(properties);
        assertNotNull(src);
        assertEquals(120000, src.getStsMaxAge());
    }
}