package works.hop.withservlet.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import works.hop.withservlet.demo.entity.Attendee;
import works.hop.withservlet.demo.entity.Speaker;
import works.hop.withservlet.demo.entity.Talk;
import works.hop.withservlet.demo.service.AttendeeService;
import works.hop.withservlet.demo.service.SpeakerService;
import works.hop.withservlet.demo.service.TalkService;
import works.hop.withservlet.resolver.input.AttendeeInput;
import works.hop.withservlet.resolver.input.SpeakerInput;
import works.hop.withservlet.resolver.input.TalkInput;

@RequiredArgsConstructor
public class Mutation implements GraphQLMutationResolver {

    private final TalkService talkService;
    private final SpeakerService speakerService;
    private final AttendeeService attendeeService;

    public Talk addTalk(TalkInput talk) {
        return talkService.saveTalk(talk);
    }

    public Speaker addSpeaker(SpeakerInput speaker) {
        return speakerService.saveSpeaker(speaker);
    }

    public Attendee addAttendee(AttendeeInput attendee) {
        return attendeeService.saveAttendee(attendee);
    }
}
