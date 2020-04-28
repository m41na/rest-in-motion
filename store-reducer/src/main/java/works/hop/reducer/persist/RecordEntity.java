package works.hop.reducer.persist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordEntity {

    private RecordKey key;
    private byte[] value;
    private Date dateCreated;
}
