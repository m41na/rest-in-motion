package works.hop.nosql.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import works.hop.scrum.entity.Scrum;

import java.util.List;
import java.util.Map;

public class UpdateChoices implements Validatable {

    public final Scrum scrum;

    @JsonCreator
    public UpdateChoices(@JsonProperty("topic") Scrum scrum) {
        this.scrum = scrum;
    }

    @Override
    public Map<String, List<String>> validate() {
        return validate(scrum);
    }
}
