package works.hop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    private Long id;
    private String firstName;
    private String lastName;
    private Login login;
    private String emailAddress;
    private Address[] addresses;
    private String phoneNumber;
    private Set<Profile> friends;
    private Set<Hobby> hobbies;
}
