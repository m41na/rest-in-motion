package works.hop.nosql.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddParticipant implements Validatable {

    @NotNull(message = "team id is not an optional field")
    private String teamId;
    @NotNull(message = "scrum name is not an optional field")
    private String scrumName;
    @NotNull(message = "scrum participant is not an optional field")
    private String participant;

    @Override
    public Map<String, List<String>> validate() {
        return validate(this);
    }
}
