package works.hop.jetty.demos.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This server accepts only one connection only since the 'handle' method is in the same thread as the 'accept'method
 */
public class BlockingServer {

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8080);
        while (true) {
            Socket s = ss.accept(); //blocking, never null
            System.out.println("connection accepted");
            handle(s);
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
