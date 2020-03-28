package works.hop.withservlet.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import works.hop.withservlet.demo.entity.Attendee;
import works.hop.withservlet.demo.entity.Speaker;
import works.hop.withservlet.demo.entity.Talk;
import works.hop.withservlet.demo.service.AttendeeService;
import works.hop.withservlet.demo.service.SpeakerService;
import works.hop.withservlet.demo.service.TalkService;

import java.util.List;

@RequiredArgsConstructor
public class Query implements GraphQLQueryResolver {

    private final TalkService talkService;
    private final SpeakerService speakerService;
    private final AttendeeService attendeeService;

    public List<Talk> allTalks() {
        return talkService.findAll();
    }

    public List<Attendee> allAttendees() {
        return this.attendeeService.findAll();
    }

    public List<Speaker> allSpeakers() {
        return this.speakerService.findAll();
    }
}
