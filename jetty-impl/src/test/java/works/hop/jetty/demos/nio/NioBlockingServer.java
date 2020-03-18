package works.hop.jetty.demos.nio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Although this server uses NIO channels for communication, it still uses a blocking 'accept' method and still
 * accepts only one connection since the 'handle' method is in the same thread as the 'accept' method
 */
public class NioBlockingServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            SocketChannel sc = ssc.accept(); //blocking, never null
            System.out.println("connection accepted - " + sc);
            handle(sc);
        }
    }

    private static void handle(SocketChannel sc) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(80);
        try {
            while (sc.read(buffer) != -1) {
                buffer.flip();
                invert(buffer);
                //write inverted content
                while (buffer.hasRemaining()) {
                    sc.write(buffer);
                }
                //reset buffer accepting new bytes
                buffer.compact();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void invert(ByteBuffer data) {
        for (int i = 0; i < data.limit(); i++) {
            data.put(i, (byte) (Character.isLetter(data.get(i)) ? data.get(i) ^ ' ' : data.get(i)));
        }
    }
}
