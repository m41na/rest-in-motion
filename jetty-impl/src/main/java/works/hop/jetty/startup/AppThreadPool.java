package works.hop.jetty.startup;

import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.function.Function;

public class AppThreadPool {

    public QueuedThreadPool createThreadPool(Function<String, String> properties) {
        int poolSize = Integer.parseInt(properties.apply("poolSize"));
        int maxPoolSize = Integer.parseInt(properties.apply("maxPoolSize"));
        int keepAliveTime = Integer.parseInt(properties.apply("keepAliveTime"));
        return new QueuedThreadPool(maxPoolSize, poolSize, keepAliveTime);
    }
}
