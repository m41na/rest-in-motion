package works.hop.reducer.scrum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.hop.model.Player;
import works.hop.reducer.persist.RecordValue;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerRecord implements RecordValue<String> {

    private String scrumId;
    private Player player;

    @Override
    public String getRecordId() {
        return scrumId;
    }

    @Override
    public void setRecordId(String id) {
        this.setScrumId(id);
    }
}
