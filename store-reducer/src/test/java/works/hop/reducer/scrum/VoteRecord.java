package works.hop.reducer.scrum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.hop.model.Vote;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRecord {

    private String scrumId;
    private Vote vote;
}
