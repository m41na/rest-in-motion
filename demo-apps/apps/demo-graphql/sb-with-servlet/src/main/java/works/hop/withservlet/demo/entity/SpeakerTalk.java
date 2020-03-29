package works.hop.withservlet.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler" })
public class SpeakerTalk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Talk.class)
    private Long talkId;
    @OneToOne(targetEntity = Speaker.class)
    private Long speakerId;

    public Long getSpeakerId() {
        return speakerId;
    }

    public Long getTalkId() {
        return talkId;
    }
}
