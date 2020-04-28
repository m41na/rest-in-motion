package works.hop.reducer.mbb;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.SerializationUtils;
import works.hop.reducer.Todo;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.persist.RecordKey;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore("The concept being tested here is far from working, so completely ignore tests")
public class MbbStateTest {

    private final String file = "votes.dat";
    private MbbState state;

    public static String bb_to_str(ByteBuffer buff) {
        byte[] bytes;
        if (buff.hasArray()) {
            bytes = buff.array();
        } else {
            bytes = new byte[buff.remaining()];
            while (buff.hasRemaining()) {
                int remaining = bytes.length;
                if (buff.remaining() < remaining)
                    remaining = buff.remaining();
                buff.get(bytes, 0, remaining);
            }
        }
        return new String(bytes);
    }

    @Before
    public void setUp() {
        state = new MbbState(file);
    }

    @Test
    public void testLoadFileFromFS() {
        Path path = MbbState.loadFileFromFS(file);
        assertTrue(path.toFile().exists());
    }

    @Test
    public void testLoadFileFromCP() {
        String file = "mock.txt";
        Path path = state.loadFileFromCP(file);
        assertTrue(path.toFile().exists());
    }

    @Test
    public void testWriteAndReadMappedFile() throws IOException {
        String text = "This is a mapped file thing";
        MbbState.writeMappedFile(text.getBytes(), file);
        //now read content
        MbbState.readMappedFile(buffer -> {
            String content = bb_to_str(buffer);
            assertEquals(text, content);
        }, file);
    }

    @Test
    public void testWriteAndReadRandomAccessFile() throws IOException {
        String file = "mock.txt";
        String text = "This is a random access file thing";
        MbbState.writeRandomAccessFile(text.getBytes(), file);
        //now read content
        MbbState.readRandomAccessFile(buffer -> {
            String content = bb_to_str(buffer);
            assertEquals(text, content);
        }, file);
    }

    @Test
    public void testSerDeserializeMbbModels() throws IOException {
        String file = "mock.txt";
        List<MbbModel> list = LongStream.range(0, 10).mapToObj(i -> {
            MbbModel model = new MbbModel();
            model.id(i);
            model.collectionKey("todos " + i);
            model.userKey("steve" + i);
            model.recordValueLength(2024);
            model.recordValueOffset(i * 1000);
            model.dateCreated(new Date());
            return model;
        }).collect(Collectors.toList());
        MbbState.serializeMbbModel(list, file);
        //read back values
        MbbState.deserializeMbbModel(model -> {
            System.out.println(model);
        }, MbbState.SIZE + MbbState.PADDING, file);
    }

    @Test
    public void testSerDeserializeEntity() throws IOException {
        RecordEntity entity = RecordEntity.builder()
                .key(RecordKey.builder().recordId(1l).collectionKey("todos").userKey("steve").build())
                .dateCreated(new Date())
                .value(SerializationUtils.serialize(Todo.builder().task("bread").completed(false).id("100").build()))
                .build();
        state.serializeEntity(entity);
        //read back entity
        state.deserializeEntity(entity.getKey(), result -> {
            System.out.println(result);
        });
    }
}