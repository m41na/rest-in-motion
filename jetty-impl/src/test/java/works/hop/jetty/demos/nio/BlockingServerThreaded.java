package works.hop.jetty.demos.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This server improves on BlockingServer by invoking the 'handle' method in a different thread to allow multiple connections
 * It's a one-connection-per-thread model, but it's limited by the number of threads that can be created
 */
public class BlockingServerThreaded {

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8080);
        while (true) {
            Socket s = ss.accept(); //blocking, never null
            System.out.println("connection accepted");
            new Thread(() -> handle(s)).start();
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
