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
public class Todo implements RecordValue<String> {

    public String id;
    public String task;
    public Boolean completed;

    @Override
    public String getRecordId() {
        return getId();
    }

    @Override
    public void setRecordId(String id) {
        setId(id);
    }
}
