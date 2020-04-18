package works.hop.scrum.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Scrum {

    private String key;
    @NonNull
    private String teamId;
    @NonNull
    private String name; //must match /^[\w_-:.@()+,=;$!*'%]+$/
    private String topic;
    private String[] choices;
    private String[] participants; //participants' name MUST NOT have whitespace
}
