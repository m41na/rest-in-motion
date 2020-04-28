package works.hop.reducer.persist;

import java.io.Serializable;

public interface RecordValue<S> extends Serializable {

    S getValue();

    void setValue(S value);
}
