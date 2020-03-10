package works.hop.core;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;

public class StartableTest {

    private Startable startable;

    @Before
    public void setUp() {
        startable = new BasicStartable();
    }

    @Test
    public void testChangesInStatusThroughLifecycle() throws Exception {
        startable.start();
        Thread.sleep(1000);
        startable.banner();
        Thread.sleep(1000);
        startable.listen(100, "localhost");
        Thread.sleep(2000);
        startable.shutdown();
    }

    class BasicStartable implements Startable {

        private String status = "stopped";
        private Thread thread;
        private CountDownLatch latch = new CountDownLatch(2);

        public BasicStartable() {
            System.out.println("status : " + status);
        }

        @Override
        public Restful rest() {
            return mock(Restful.class);
        }

        @Override
        public String status() {
            return this.status;
        }

        @Override
        public void banner() {
            System.out.println("showing banner");
        }

        @Override
        public void start() throws Exception {
            thread = new Thread(() -> {
                status = "starting";
                System.out.println("status : " + status);
                try {
                    latch.await();
                    status = "stopping";
                    System.out.println("status : " + status);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            });
            thread.start();
        }

        @Override
        public void listen(Integer port, String host) {
            listen(port, host, System.out::println);
        }

        @Override
        public void listen(Integer port, String host, Consumer<String> result) {
            latch.countDown();
            status = "Running";
            System.out.println("status : " + status);
        }

        @Override
        public void shutdown() throws Exception {
            latch.countDown();
            status = "stopped";
            System.out.println("status : " + status);
        }
    }
}