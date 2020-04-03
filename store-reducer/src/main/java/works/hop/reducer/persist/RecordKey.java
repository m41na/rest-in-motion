package works.hop.reducer.persist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordKey {

    private Long id;
    private String userKey;
    private String collectionKey;
}
