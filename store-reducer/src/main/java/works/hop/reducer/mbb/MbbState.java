package works.hop.reducer.mbb;

import org.springframework.util.SerializationUtils;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.persist.RecordKey;
import works.hop.reducer.persist.RecordValue;
import works.hop.reducer.state.State;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Memory boundaries for stored entity
 * id: long                     8 bytes
 * useKey: char[]               2x128 bytes
 * collectionKey: char          2x512 bytes
 * dateCreated: long            8 bytes
 * recordValue: Blob
 * - recordValueOffset: long   8 bytes
 * - recordValueLength: int    4 bytes
 * -----------------------------------------
 * Bytes required per entry     1308 bytes
 */
public class MbbState implements State<Map<String, Map<String, List<RecordEntity>>>> {

    public static final Integer SIZE = 1308;
    public static final Integer PADDING = 239;
    private final String fileName;
    private final String blobFile;

    public MbbState(String file) {
        this.fileName = file;
        this.blobFile = file + "_blobs";
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(final byte[] bytes, final int offset) {
        long result = 0;
        for (int i = offset; i < Long.BYTES + offset; i++) {
            result <<= Long.BYTES;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static byte[] intToBytes(final int data) {
        return new byte[]{
                (byte) ((data >> 24) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff),
        };
    }

    private int bytesToInt(byte[] bytes, int i) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8 ) |
                ((bytes[3] & 0xFF) << 0 );
    }

    public static Path loadFileFromFS(String file) {
        return Path.of(System.getProperty("user.home"), "data", file);
    }

    public static Path loadFileFromCP(String file) {
        URL fileUrl = MbbState.class.getResource(file);
        try {
            return Paths.get(fileUrl.getPath());
        } catch (Exception e) {
            File absFile = new File(fileUrl.getPath()).getAbsoluteFile();
            return Paths.get(absFile.toURI());
        }
    }

    public static void readMappedFile(Consumer<ByteBuffer> consumer, String file) throws IOException {
        Path pathToRead = loadFileFromFS(file);
        try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(pathToRead, EnumSet.of(StandardOpenOption.READ))) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            if (mappedByteBuffer != null) {
                consumer.accept(mappedByteBuffer);
            }
        }
    }

    public static void readRandomAccessFile(Consumer<ByteBuffer> consumer, String file) throws IOException {
        try (RandomAccessFile raFile = new RandomAccessFile(loadFileFromFS(file).toFile(), "r")) {
            FileChannel fileChannel = raFile.getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            if (mappedByteBuffer != null) {
                consumer.accept(mappedByteBuffer);
            }
        }
    }

    public static void writeMappedFile(byte[] bytes, String file) throws IOException {
        Path pathToRead = loadFileFromFS(file);
        try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(pathToRead, EnumSet.of(
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING))) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
            if (mappedByteBuffer != null) {
                mappedByteBuffer.put(bytes);
            }
        }
    }

    public static void writeRandomAccessFile(byte[] bytes, String file) throws IOException {
        try (RandomAccessFile raFile = new RandomAccessFile(loadFileFromFS(file).toFile(), "rw")) {
            MappedByteBuffer mappedByteBuffer = raFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
            if (mappedByteBuffer != null) {
                mappedByteBuffer.put(bytes);
            }
        }
    }

    public static void serializeMbbModel(List<MbbModel> list, String file) throws IOException {
        int length = list.size() * (SIZE + PADDING);
        try (RandomAccessFile raFile = new RandomAccessFile(loadFileFromFS(file).toFile(), "rw")) {
            MappedByteBuffer mappedByteBuffer = raFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);
            if (mappedByteBuffer != null) {
                for (MbbModel model : list) {
                    byte[] bytes = SerializationUtils.serialize(model);
                    System.out.println("bytes size: " + bytes.length);
                    mappedByteBuffer.put(bytes);
                }
            }
        }
    }

    public static void deserializeMbbModel(Consumer<MbbModel> consumer, int batchSize, String file) throws IOException {
        try (RandomAccessFile raFile = new RandomAccessFile(loadFileFromFS(file).toFile(), "r");
             FileChannel fileChannel = raFile.getChannel()) {
            int offset = 0;
            ByteBuffer buffer = ByteBuffer.allocate(batchSize);
            while (offset < fileChannel.size()) {
                int size = fileChannel.read(buffer);
                buffer.flip();
                MbbModel record = (MbbModel) SerializationUtils.deserialize(buffer.array());
                buffer.clear();
                offset += size;
                consumer.accept(record);
            }
        }
    }

    public void serializeEntity(RecordEntity entity) throws IOException {
        try (RandomAccessFile raBlob = new RandomAccessFile(loadFileFromFS(blobFile).toFile(), "rw");
             RandomAccessFile raFile = new RandomAccessFile(loadFileFromFS(fileName).toFile(), "rw")) {
            long raBlobEnd = 0;//raBlob.length();
            //raBlob.seek(endIndex); //point at the end of the file to append
            byte[] recordBytes = SerializationUtils.serialize(entity.getValue());
            MappedByteBuffer raMappedByteBuffer = raBlob.getChannel().map(FileChannel.MapMode.READ_WRITE, raBlobEnd, recordBytes.length);
            if (raMappedByteBuffer != null) {
                System.out.println("record bytes size: " + recordBytes.length);
                raMappedByteBuffer.put(recordBytes);
            }

            int modelSize = (SIZE + PADDING);
            long raFileEnd = 0;//raFile.length();
            //raFile.seek(raFileEnd); //point at the end of the file to append
            MappedByteBuffer mappedByteBuffer = raFile.getChannel().map(FileChannel.MapMode.READ_WRITE, raFileEnd, modelSize);
            if (mappedByteBuffer != null) {
                MbbModel model = new MbbModel();
                model.id(entity.getKey().getId());
                model.collectionKey(entity.getKey().getCollectionKey());
                model.userKey(entity.getKey().getUserKey());
                model.recordValueLength(recordBytes.length);
                model.recordValueOffset(raBlobEnd);
                model.dateCreated(entity.getDateCreated());
                byte[] modelBytes = SerializationUtils.serialize(model);
                System.out.println("model bytes size: " + modelBytes.length);
                mappedByteBuffer.put(modelBytes);
            }
        }
    }

    public void deserializeEntity(RecordKey key, Consumer<RecordEntity> consumer) throws IOException {
        int modelSize = (SIZE + PADDING);
        try (RandomAccessFile raFile = new RandomAccessFile(loadFileFromFS(fileName).toFile(), "r");
             RandomAccessFile raBlob = new RandomAccessFile(loadFileFromFS(blobFile).toFile(), "rw");
             FileChannel raFileChannel = raFile.getChannel();
             FileChannel raBlobChannel = raBlob.getChannel()) {
            int offset = 0;
            ByteBuffer modelBuffer = ByteBuffer.allocate(modelSize);
            MbbModel record = null;
            while (offset < raFileChannel.size()) {
                int size = raFileChannel.read(modelBuffer);
                modelBuffer.flip();
                record = (MbbModel) SerializationUtils.deserialize(modelBuffer.array());
                modelBuffer.clear();
                offset += size;
                if(bytesToLong(record.getId(), 0) == key.getId().longValue()){
                    break;
                }
            }

            if(record != null){
                raBlob.seek(bytesToLong(record.getRecordValueOffset(), 0)); //point to start of record
                ByteBuffer recordBuffer = ByteBuffer.allocate(bytesToInt(record.getRecordValueLength(), 0));
                int len = raBlobChannel.read(recordBuffer);
                System.out.println("blob length read -> " + len);
                RecordValue recordValue = (RecordValue) SerializationUtils.deserialize(recordBuffer.array());
                //re-create entity
                RecordEntity recordEntity = RecordEntity.builder().value(recordValue)
                        .key(key)
                        .dateCreated(new Date(bytesToLong(record.getDateCreated(), 0)))
                        .build();
                consumer.accept(recordEntity);
            }
        }
    }

    @Override
    public Map<String, Map<String, List<RecordEntity>>> get() {
        return null;
    }
}
