package works.hop.scrum.demo;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.nio.file.StandardOpenOption.*;
import static org.junit.Assert.assertEquals;

public class FileNioTests {

    public static String readContent(Path file) throws IOException, ExecutionException, InterruptedException {
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(file, StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Future<Integer> operation = fileChannel.read(buffer, 0);

        // run other code as operation continues in background
        operation.get();

        String fileContent = new String(buffer.array()).trim();
        buffer.clear();
        return fileContent;
    }

    @Test
    public void givenFilePath_whenReadsContentWithFuture_thenCorrect() throws IOException, ExecutionException, InterruptedException {
        Path path = Paths.get(URI.create(this.getClass().getResource("/sample.txt").toString()));
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Future<Integer> operation = fileChannel.read(buffer, 0);
        // run other code as operation continues in background
        operation.get();
        String fileContent = new String(buffer.array()).trim();
        buffer.clear();
        assertEquals(fileContent, "Hello world!");
    }

    @Test
    public void givenPathAndContent_whenWritesToFileWithFuture_thenCorrect() throws ExecutionException, InterruptedException, IOException {
        String fileName = UUID.randomUUID().toString();
        Path path = Paths.get(fileName);
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, WRITE, CREATE, DELETE_ON_CLOSE);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("hello world".getBytes());
        buffer.flip();

        Future<Integer> operation = fileChannel.write(buffer, 0);
        buffer.clear();

        //run other code as operation continues in background
        operation.get();

        String content = readContent(path);
        assertEquals("hello world", content);
    }

    @Test
    public void givenPath_whenReadsContentWithCompletionHandler_thenCorrect() throws IOException {
        Path path = Paths.get(URI.create(this.getClass().getResource("/sample.txt").toString()));
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        fileChannel.read(
                buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        // result is number of bytes read
                        // attachment is the buffer containing content
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                    }
                });
    }

    @Test
    public void givenPathAndContent_whenWritesToFileWithHandler_thenCorrect() throws IOException {
        String fileName = UUID.randomUUID().toString();
        Path path = Paths.get(fileName);
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, WRITE, CREATE, DELETE_ON_CLOSE);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("hello world".getBytes());
        buffer.flip();

        fileChannel.write(
                buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {

                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        // result is number of bytes written
                        // attachment is the buffer
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {

                    }
                });
    }
}
