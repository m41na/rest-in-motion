package works.hop.nosql.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterOrganizer implements Validatable {

    private String id;
    private String firstName;
    private String lastName;
    @NotNull(message = "organizer's email is not an optional field")
    private String emailAddress;
    private String phoneNumber;
    @PastOrPresent(message = "date created can only be present or in the past")
    private Date dateCreated;

    @Override
    public Map<String, List<String>> validate() {
        return validate(this);
    }
}
