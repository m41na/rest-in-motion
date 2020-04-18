package works.hop.nosql.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import works.hop.scrum.entity.Scrum;

public class InviteParticipant {

    public final Scrum scrum;
    public final String topic;
    public final String participant;

    @JsonCreator
    public InviteParticipant(@JsonProperty("scrum") Scrum scrum, @JsonProperty("topic") String topic, @JsonProperty("participant") String participant) {
        this.scrum = scrum;
        this.topic = topic;
        this.participant = participant;
    }
}
