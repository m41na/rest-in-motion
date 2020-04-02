package works.hop.reducer.persist;

import java.io.Serializable;

public interface RecordValue extends Serializable {

    Long getId();

    void setId(Long id);
}
