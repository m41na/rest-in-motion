package works.hop.reducer.scrum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.hop.model.Player;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerRecord {

    private String scrumId;
    private Player player;
}
