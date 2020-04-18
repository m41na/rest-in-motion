package works.hop.nosql.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterTeam implements Validatable {

    private String id;
    @NotNull(message = "team title is not an optional field")
    private String title;
    private String organization;
    @Email
    @NotNull(message = "team organizer's email is not an optional field")
    private String organizer;
    @PastOrPresent(message = "date created can only be now or in the past")
    @NotNull(message = "date created is not an optional field")
    private Date dateCreated;

    @Override
    public Map<String, List<String>> validate() {
        return validate(this);
    }
}
