package works.hop.cache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class FileMap {

    public static void main(String[] args) throws IOException {
        byte[] text = "This is a fixed length string".getBytes();
        Path p = Path.of(System.getProperty("user.dir"), "core-api/target", "cache.dat");
        File f = p.toFile();
        try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
            FileChannel fc = raf.getChannel();
            ByteBuffer fileArea = fc.map(FileChannel.MapMode.READ_WRITE, 0, 128);
            fileArea.put(text);
        }
    }
}
