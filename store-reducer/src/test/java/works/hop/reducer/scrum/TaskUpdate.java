package works.hop.reducer.scrum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.hop.reducer.persist.RecordValue;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUpdate implements RecordValue<String> {

    private String scrumId;
    private String task;

    @Override
    public String getRecordId() {
        return scrumId;
    }

    @Override
    public void setRecordId(String id) {
        this.setScrumId(id);
    }
}
