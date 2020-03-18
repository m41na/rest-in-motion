package works.hop.jetty.startup;

import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AppThreadPoolTest {

    private AppThreadPool threadPool;
    private Map<String, String> map = new HashMap<>() {
        {
            put("poolSize", "10");
            put("maxPoolSize", "20");
            put("keepAliveTime", "30");
        }
    };
    private Function<String, String> properties = s -> map.getOrDefault(s, s);

    @Test
    public void createThreadPool() {
        QueuedThreadPool pool = AppThreadPool.createThreadPool(properties);
        assertNotNull(pool);
        assertEquals(20, pool.getMaxThreads());
        assertEquals(10, pool.getMinThreads());
        //TODO: test keepAliveTime assertEquals(30, pool.);
    }
}