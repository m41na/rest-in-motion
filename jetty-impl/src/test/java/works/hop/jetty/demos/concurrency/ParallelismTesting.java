package works.hop.jetty.demos.concurrency;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Collection.stream();
 * Collection.parallelStream()
 * Stream.of(T...)
 * Stream.iterator(seed, UnaryOperator<T>)
 * Stream.generate(Supplier<T>)
 * Stream.parallel();
 * Stream.sequential()
 */
public class ParallelismTesting {

    static long fibonacci(long n) {
        //System.out.println("fib for " + n);
        if (n == 0) {
            return 0;
        }
        if (n == 1 || n == 2) {
            return 1;
        }
        return fibonacci(n - 2) + fibonacci(n - 1);
    }

    static int doubleIt(int n) {
        try {
            Thread.sleep(100);
            System.out.printf("%s with n = %d%n", Thread.currentThread().getName(), n);
        } catch (InterruptedException ignore) {
        }
        return n * 2;
    }

    public static void main(String[] args) {
        List<Long> values = Arrays.asList(2l, 3l, 4l, 5l, 6l, 7l);
        values.forEach(n -> System.out.print(fibonacci(n) + ","));
        System.out.println("\n------------------");

        Long sum1 = values.stream().reduce(0l, (acc, n) -> acc + fibonacci(n));
        System.out.println("sequential fib sum1 = " + sum1);
        System.out.println("------------------");

        Long sum2 = values.stream().parallel().map(n -> fibonacci(n)).reduce(0l, (acc, val) -> acc + val);
        System.out.println("parallel fib sum2 = " + sum2);
        System.out.println("------------------");

        List<CompletableFuture<Long>> futures = values.stream()
                .map(n -> CompletableFuture.supplyAsync(() -> fibonacci(n)))
                .collect(Collectors.toList());

        sum2 = futures.stream().map(CompletableFuture::join).reduce(0l, (acc, val) -> acc + val);
        System.out.println("futures fib sum2 = " + sum2);
        System.out.println("------------------");

        List<Integer> list1 = Arrays.asList(3, 1, 4, 1, 5, 9);
        //non-functional, with shared state
        int sum3 = 0;
        for (int i : list1) {
            sum3 += i;
        }
        System.out.println("for...loop sum3 = " + sum3);
        System.out.println("------------------");

        int sum4 = IntStream.of(3, 1, 4, 1, 5, 9).sum();
        System.out.println("stream.sum sum4 = " + sum4);
        System.out.println("------------------");

        Instant before = Instant.now();
        int sum5 = IntStream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5, 7, 9, 7)
                .parallel()
                .map(ParallelismTesting::doubleIt)
                .sum();
        Instant after = Instant.now();
        Duration duration = Duration.between(before, after);
        System.out.println("sum5 = " + sum5 + ". Time taken = " + duration.toMillis() + " ms");
        System.out.println("------------------");
    }

    @Test
    public void sequentialStreamOf() {
        assertFalse(Stream.of(3, 1, 4, 1, 5, 9).isParallel());
    }

    @Test
    public void sequentialIterateStream() {
        assertFalse(Stream.iterate(1, n -> n + 1).isParallel());
    }

    @Test
    public void sequentialGenerateStream() {
        assertFalse(Stream.generate(Math::random).isParallel());
    }

    @Test
    public void sequentialCollectionStream() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9);
        assertFalse(numbers.stream().isParallel());
    }

    @Test
    public void parallelMethodOnStream() {
        assertTrue(Stream.of(3, 1, 4, 1, 5, 9).parallel().isParallel());
    }

    @Test
    public void parallelStreamMethodOnCollection() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9);
        assertFalse(numbers.parallelStream().isParallel());
    }

    @Test
    public void parallelStreamThenSequential() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9);
        assertFalse(numbers.parallelStream().sequential().isParallel());
    }

    @Test
    public void switchingParallelStreamToSequentialInSameStream() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9);
        List<Integer> result = numbers.parallelStream()
                .map(n -> n * 2)
                .peek(n -> System.out.printf("%s processing %d%n", Thread.currentThread().getName(), n))
                .sequential()
                .sorted()
                .collect(Collectors.toList());
        System.out.println(result);
    }
}
