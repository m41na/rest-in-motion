package works.hop.reducer.persist;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecordCriteria {

    private final Long id;
    private final String userKey;
    private final String collectionKey;
}
