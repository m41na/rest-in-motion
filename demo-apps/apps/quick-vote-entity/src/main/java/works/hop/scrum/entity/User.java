package works.hop.scrum.entity;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private String id;
    private String firstName;
    private String lastName;
    @NonNull
    private String emailAddress;
    private String phoneNumber;
    @NonNull
    private Date dateCreated;
}
