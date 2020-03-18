package works.hop.jetty.demos.nio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This server tries to improve on NioBlockingServer by using a non-blocking 'accept' method but employs a TERRIBLE
 * strategy of continuously polling the 'accept' for new connection requests. View CPU usage to see how bad
 */
public class NonBlockingPolling {

    public static void main(String[] args) throws IOException {
        Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8080));
        serverChannel.configureBlocking(false); //configure as non-blocking
        while (true) {
            SocketChannel clientChannel = serverChannel.accept(); //non-blocking, almost always null
            if (clientChannel != null) {
                System.out.println("connection accepted - " + clientChannel);
                clientChannel.configureBlocking(false); //configure as non-clocking as well
                sockets.putIfAbsent(clientChannel, ByteBuffer.allocateDirect(80));
                sockets.keySet().removeIf(socket -> !socket.isOpen()); //remove if not open
                sockets.forEach(NonBlockingPolling::handle);
            }
        }
    }

    private static void handle(SocketChannel clientChannel, ByteBuffer buffer) {
        try {
            int data = clientChannel.read(buffer);
            if (data == -1) {
                close(clientChannel);
            } else if (data != 0) {
                System.out.println("incoming data from client socket");
                buffer.flip();
                invert(buffer);
                while (buffer.hasRemaining()) {
                    clientChannel.write(buffer);
                }
                buffer.compact(); //reset buffer for reading into
            }
        } catch (IOException e) {
            close(clientChannel);
            throw new UncheckedIOException(e);
        }
    }

    private static void close(SocketChannel sc) {
        try {
            System.out.println("closing client socket");
            sc.close();
        } catch (IOException e) {
            e.printStackTrace(System.err); //ignore exception
        }
    }

    private static void invert(ByteBuffer data) {
        for (int i = 0; i < data.limit(); i++) {
            data.put(i, (byte) (Character.isLetter(data.get(i)) ? data.get(i) ^ ' ' : data.get(i)));
        }
    }
}
