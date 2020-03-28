package works.hop.withcontroller.resolver;

import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import works.hop.withcontroller.demo.service.AttendeeService;
import works.hop.withcontroller.demo.service.SpeakerService;
import works.hop.withcontroller.demo.service.TalkService;

@Component
public class GraphQLDataFetchers {

    @Autowired
    private SpeakerService speakerService;
    @Autowired
    private TalkService talkService;
    @Autowired
    private AttendeeService attendeeService;

    public DataFetcher allTalks() {
        return env -> talkService.findAll();
    }

    public DataFetcher allSpeakers() {
        return env -> speakerService.findAll();
    }

    public DataFetcher allAttendees() {
        return env -> attendeeService.findAll();
    }
}
