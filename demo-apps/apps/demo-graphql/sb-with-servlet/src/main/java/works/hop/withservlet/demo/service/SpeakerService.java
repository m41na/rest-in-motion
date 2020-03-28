package works.hop.withservlet.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import works.hop.withservlet.demo.entity.Speaker;
import works.hop.withservlet.demo.entity.SpeakerTalk;
import works.hop.withservlet.demo.entity.Talk;
import works.hop.withservlet.demo.repo.SpeakerRepo;
import works.hop.withservlet.demo.repo.SpeakerTalkRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpeakerService {

    @Autowired
    private SpeakerRepo speakerRepo;
    @Autowired
    private SpeakerTalkRepo speakerTalkRepo;

    public Speaker saveSpeaker(Speaker speaker) {
        return speakerRepo.save(speaker);
    }

    public List<Speaker> findAll() {
        return speakerRepo.findAll();
    }

    public List<Speaker> findAllSpeakersForTalk(Talk talk) {
        List<SpeakerTalk> at = speakerTalkRepo.findAllByTalkId(talk.getId());
        return at.stream().map(e -> speakerRepo.findById(e.getSpeakerId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Speaker save(Speaker speaker) {
        return speakerRepo.save(speaker);
    }
}
