package works.hop.scrum.entity;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Player {

    private String key;
    @NonNull
    private String teamId;
    @NonNull
    private String name;
    private Boolean active;
    @NonNull
    private Date lastJoined;
}
