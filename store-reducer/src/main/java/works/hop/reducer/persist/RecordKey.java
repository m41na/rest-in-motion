package works.hop.reducer.persist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordKey<T> {

    private T recordId;
    private String userKey;
    private String collectionKey;
}
