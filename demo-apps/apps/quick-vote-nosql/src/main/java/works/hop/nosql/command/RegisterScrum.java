package works.hop.nosql.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterScrum implements Validatable {

    private String key;
    @NotNull(message = "team id is not an optional field")
    private String teamId;
    @Pattern(regexp = "^[\\w_\\-:.@()+,=;$!*'%]+$", message = "scrum name should match '^[\\w_\\-:.@()+,=;$!*'%]+$'")
    @NotNull(message = "scrum name is not an optional field")
    private String name;
    private String topic;
    private String[] choices;
    private String[] participants;

    @Override
    public Map<String, List<String>> validate() {
        return validate(this);
    }
}
