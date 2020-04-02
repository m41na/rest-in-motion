package works.hop.reducer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.hop.reducer.persist.RecordValue;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo implements RecordValue {

    public Long id;
    public String task;
    public Boolean completed;
}
