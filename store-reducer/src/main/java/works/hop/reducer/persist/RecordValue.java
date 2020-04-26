package works.hop.reducer.persist;

import java.io.Serializable;

public interface RecordValue<T> extends Serializable {

    T getRecordId();

    void setRecordId(T id);
}
