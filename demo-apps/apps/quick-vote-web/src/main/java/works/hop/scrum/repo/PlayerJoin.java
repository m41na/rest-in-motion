package works.hop.scrum.repo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.hop.scrum.domain.Player;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerJoin {

    private String scrumId;
    private Player player;
}
