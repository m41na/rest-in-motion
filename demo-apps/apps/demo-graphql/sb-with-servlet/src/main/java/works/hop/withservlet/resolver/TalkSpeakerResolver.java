package works.hop.withservlet.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import works.hop.withservlet.demo.entity.Speaker;
import works.hop.withservlet.demo.entity.Talk;
import works.hop.withservlet.demo.service.SpeakerService;

import java.util.List;

@RequiredArgsConstructor
public class TalkSpeakerResolver implements GraphQLResolver<Talk> {

    private final SpeakerService speakerService;

    public List<Speaker> findSpeakers(Talk talk) {
        return speakerService.findAllSpeakersForTalk(talk);
    }
}
