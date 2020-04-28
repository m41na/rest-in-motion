package works.hop.scrum.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    private String email;   //player's unique handle
    private String name;    //player's display name
    private Boolean joined; //joined flag
}
