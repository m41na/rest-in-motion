package works.hop.nosql.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemoveParticipant {

    private String teamId;
    private String scrumName;
    private String participant;
}
