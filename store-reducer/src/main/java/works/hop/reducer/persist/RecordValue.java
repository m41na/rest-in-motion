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
public class RecordValue {

    private Long id;
    private String userKey;
    private String collectionKey;
    private Object dataValue;
    private Date dateCreated;
}
