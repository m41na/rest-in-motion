package works.hop.withcontroller.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler" })
public class AttendeeTalk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Talk.class)
    private Long talkId;
    @OneToOne(targetEntity = Attendee.class)
    private Long attendeeId;

    public Long getAttendeeId() {
        return talkId;
    }

    public Long getTalkId() {
        return attendeeId;
    }
}
