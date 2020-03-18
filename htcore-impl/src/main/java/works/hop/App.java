package works.hop;

import org.apache.http.impl.nio.reactor.SessionInputBufferImpl;
import org.apache.http.impl.nio.reactor.SessionOutputBufferImpl;
import org.apache.http.nio.reactor.SessionInputBuffer;
import org.apache.http.nio.reactor.SessionOutputBuffer;
import org.apache.http.util.CharArrayBuffer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class App {

    static String sampleFile = "./htcore-impl/target/sample.txt";

    public static void main(String[] args) {
        writeToByteChannel(sampleFile, "this is some boring text");
        readFromByteChannel(sampleFile);
    }

    public static void playWithChannels() {
        try (ReadableByteChannel channel1 = Channels.newChannel(new FileInputStream(sampleFile));
             WritableByteChannel channel2 = Channels.newChannel(new FileOutputStream(sampleFile))) {

            SessionInputBuffer inbuffer = new SessionInputBufferImpl(8 * 1024);
            SessionOutputBuffer outbuffer = new SessionOutputBufferImpl(8 * 1024);

            CharArrayBuffer linebuf = new CharArrayBuffer(1024);
            boolean endOfStream = false;
            int bytesRead = inbuffer.fill(channel1);
            if (bytesRead == -1) {
                endOfStream = true;
            }
            if (inbuffer.readLine(linebuf, endOfStream)) {
                outbuffer.writeLine(linebuf);
            }
            if (outbuffer.hasData()) {
                outbuffer.flush(channel2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFromByteChannel(String file) {
        try (InputStream in = new FileInputStream(file);
             ReadableByteChannel channel = Channels.newChannel(in)) {
            //allocate byte buffer size
            ByteBuffer byteBuffer = ByteBuffer.allocate(512);
            while (channel.read(byteBuffer) > 0) {
                //limit is set to current position and position is set to zero
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()) {
                    char ch = (char) byteBuffer.get();
                    System.out.print(ch);
                    byteBuffer.compact();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToByteChannel(String file, String content) {
        try (OutputStream in = new FileOutputStream(file);
             WritableByteChannel channel = Channels.newChannel(in)) {
            //allocate byte buffer size
            ByteBuffer byteBuffer = ByteBuffer.wrap(content.getBytes());
            byteBuffer.flip();
            channel.write(byteBuffer);
            byteBuffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
