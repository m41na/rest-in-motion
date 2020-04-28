package works.hop.scrum.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.hop.scrum.domain.Player;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scrum {

    private String resourceId;          //used in url to identify scrum. required
    private String organizer;           //organizer's email handle. required
    private String title;               //optional name for the scrum
    private String task;                //current task under voting
    private List<Player> players;       //list of participants
    private List<String> choices;       //list of acceptable values
    private Map<String, String> votes;  //votes submitted for current task
    private Boolean locked;             //toggle accepting votes or not
}
