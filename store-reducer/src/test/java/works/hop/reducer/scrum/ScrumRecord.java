package works.hop.reducer.scrum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.hop.model.Scrum;
import works.hop.reducer.persist.RecordValue;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScrumRecord implements RecordValue<String> {

    private Scrum scrum;

    @Override
    public String getRecordId() {
        return scrum.getResourceId();
    }

    @Override
    public void setRecordId(String id) {
        this.scrum.setResourceId(id);
    }
}
