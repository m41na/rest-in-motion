package works.hop.nosql.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import works.hop.scrum.entity.Vote;

import java.util.List;
import java.util.Map;

public class SubmitVote implements Validatable {

    private final Vote vote;

    @JsonCreator
    public SubmitVote(@JsonProperty("post") Vote vote) {
        this.vote = vote;
    }

    @Override
    public Map<String, List<String>> validate() {
        return validate(vote);
    }
}
