package works.hop.scrum.entity;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vote {

    private String key;
    @NonNull
    private String scrumKey;
    @NonNull
    private String participant; //participant name MUST NOT have whitespace
    @NonNull
    private String topic;
    @NonNull
    private String value;
    private Boolean locked;
    @NonNull
    private Date timeVoted;
}
