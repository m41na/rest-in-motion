package works.hop.reducer.mbb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import static works.hop.reducer.mbb.MbbState.intToBytes;
import static works.hop.reducer.mbb.MbbState.longToBytes;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MbbModel implements Serializable {

    private byte[] id = new byte[8]; //long
    private byte[] userKey = new byte[256]; //char[128]
    private byte[] collectionKey = new byte[1024]; //char[512]
    private byte[] dateCreated = new byte[8]; //long
    private byte[] recordValueOffset = new byte[8]; //long
    private byte[] recordValueLength = new byte[4]; //int

    public void id(long id) {
        this.id = longToBytes(id);
    }

    public void userKey(String user) {
        byte[] src = user.getBytes();
        System.arraycopy(src, 0, userKey, 0, src.length);
    }

    public void collectionKey(String collection) {
        byte[] src = collection.getBytes();
        System.arraycopy(src, 0, userKey, 0, src.length);
    }

    public void dateCreated(Date date) {
        this.dateCreated = longToBytes(date.getTime());
    }

    public void recordValueOffset(long offset) {
        this.recordValueOffset = longToBytes(offset);
    }

    public void recordValueLength(int length) {
        this.recordValueLength = intToBytes(length);
    }
}
