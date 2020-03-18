package works.hop.jetty.demos.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This server tries to improve on NonBlockingPolling by discarding the polling strategy and instead employing
 * a Selector which channels register with and notify of i/o events
 */
public class NonBlockingSelector {

    static Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8080));
        serverChannel.configureBlocking(false); //configure as non-blocking

        //create selector for I/O events
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select(); //blocking for i/o event to happen
            Set<SelectionKey> keys = selector.selectedKeys();
            for (Iterator<SelectionKey> iter = keys.iterator(); iter.hasNext(); ) {
                SelectionKey key = iter.next();
                iter.remove();
                try {
                    //examine key for correct operation
                    if (key.isValid()) {
                        if (key.isAcceptable()) {
                            accept(key);
                        } else if (key.isReadable()) {
                            read(key);
                        } else if (key.isWritable()) {
                            write(key);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
                //clean up closed channels
                sockets.keySet().removeIf(client -> !client.isOpen()); //remove if not open
            }
        }
    }

    private static void write(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = sockets.get(clientChannel);
        clientChannel.write(buffer);
        if (!buffer.hasRemaining()) {
            buffer.compact();
            //register read interest since writing is complete
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private static void read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = sockets.get(clientChannel);
        int data = clientChannel.read(buffer);
        if (data == -1) {
            clientChannel.close();
        } else {
            System.out.println("incoming data from client channel");
            buffer.flip();
            invert(buffer);
            //register write interest since reading is completed
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    private static void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept(); //non-blocking, but NEVER null
        System.out.println("connection accepted - " + clientChannel);
        clientChannel.configureBlocking(false); //configure as non-clocking as well
        clientChannel.register(key.selector(), SelectionKey.OP_READ);
        sockets.putIfAbsent(clientChannel, ByteBuffer.allocateDirect(80));
    }

    private static void invert(ByteBuffer data) {
        for (int i = 0; i < data.limit(); i++) {
            data.put(i, (byte) (Character.isLetter(data.get(i)) ? data.get(i) ^ ' ' : data.get(i)));
        }
    }
}
