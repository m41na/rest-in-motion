package works.hop.reducer.scrum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoicesUpdate {

    private String scrumId;
    private String[] choices;
}
