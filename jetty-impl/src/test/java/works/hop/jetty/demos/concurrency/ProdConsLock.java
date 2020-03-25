package works.hop.jetty.demos.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProdConsLock {

    private static Lock lock = new ReentrantLock();
    private static Condition increased = lock.newCondition();
    private static Condition decreased = lock.newCondition();
    private static Integer MAX_COUNT = 10;
    private static Integer MIN_COUNT = 0;
    private static Integer count = 10;

    public static void main(String[] args) throws InterruptedException {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        new Thread(() -> {
            try {
                producer.produce();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }).start();

        new Thread(() -> {
            try {
                consumer.consume();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }).start();

        Thread.currentThread().join();
    }

    static class Producer {

        public void produce() throws InterruptedException {
            lock.lock();
            try {
                while (count == MAX_COUNT)
                    decreased.await();

                increment();
                increased.signal();
            } finally {
                lock.unlock();
                System.out.println("released lock");
            }
        }

        public void increment() throws InterruptedException {
            count++;
            System.out.println("inc - " + count);
            //Thread.sleep(100);
        }
    }

    static class Consumer {

        public void consume() throws InterruptedException {
            lock.lock();
            try {
                while (count == MIN_COUNT)
                    increased.await();

                decrement();
                decreased.signal();
            } finally {
                lock.unlock();
                System.out.println("released lock");
            }
        }

        public void decrement() throws InterruptedException {
            count--;
            System.out.println("dec - " + count);
            //Thread.sleep(99);
        }
    }
}
