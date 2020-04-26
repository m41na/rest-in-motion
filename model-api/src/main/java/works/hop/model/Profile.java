package works.hop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    private String firstName;
    private String lastName;
    private Integer age;
    private Float height;
    private Boolean registered;
    private List<Phone> phoneNumbers;
    private List<Integer> integers;
    private List<String> strings;
    private List<Float> floats;
    private List<Boolean> booleans;
}
