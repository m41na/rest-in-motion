package works.hop.reducer.scrum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TitleUpdate {

    private String scrumId;
    private String title;
}
