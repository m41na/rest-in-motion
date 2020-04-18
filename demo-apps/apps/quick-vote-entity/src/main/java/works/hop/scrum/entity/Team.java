package works.hop.scrum.entity;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Team {

    private String id;
    @NonNull
    private String title;
    private String organization;
    @NonNull
    private String organizer;
    @NonNull
    private Date dateCreated;
}
