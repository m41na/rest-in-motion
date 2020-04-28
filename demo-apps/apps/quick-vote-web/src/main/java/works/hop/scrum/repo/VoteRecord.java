package works.hop.scrum.repo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.hop.scrum.domain.Vote;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRecord {

    private String scrumId;
    private Vote vote;
}
