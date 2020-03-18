package works.hop.jetty.demos.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This server attempts to improve on BlockingServerThreaded by reusing threads from a connection pools to avoid
 * crashing when too many connections requests are made - some connections will however just be dropped instead
 */
public class BlockingServerExecutor {

    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(100);
        ServerSocket ss = new ServerSocket(8080);
        while (true) {
            Socket s = ss.accept(); //blocking, never null
            System.out.println("connection accepted");
            pool.submit(() -> handle(s));
        }
    }

    private static void handle(Socket s) {
        try (InputStream is = s.getInputStream();
             OutputStream os = s.getOutputStream()) {
            int data;
            while ((data = is.read()) != -1) {
                data = invert(data);
                os.write(data);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static int invert(int data) {
        return Character.isLetter(data) ?
                data ^ ' ' : data;
    }
}
