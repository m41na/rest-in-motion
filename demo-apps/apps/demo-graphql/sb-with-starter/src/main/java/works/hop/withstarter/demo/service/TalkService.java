package works.hop.withstarter.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import works.hop.withstarter.demo.entity.*;
import works.hop.withstarter.demo.repo.AttendeeTalkRepo;
import works.hop.withstarter.demo.repo.SpeakerTalkRepo;
import works.hop.withstarter.demo.repo.TalkRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TalkService {

    @Autowired
    private TalkRepo talkRepo;
    @Autowired
    private SpeakerTalkRepo speakerTalkRepo;
    @Autowired
    private AttendeeTalkRepo attendeeTalkRepo;

    public List<Talk> findAll() {
        return talkRepo.findAll();
    }

    public List<Talk> findAllTalksForSpeaker(Speaker speaker) {
        List<SpeakerTalk> st = speakerTalkRepo.findAllBySpeakerId(speaker.getId());
        return st.stream().map(e -> talkRepo.findById(e.getTalkId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<Talk> findAllTalksForAttendee(Attendee attendee) {
        List<AttendeeTalk> st = attendeeTalkRepo.findAllByAttendeeId(attendee.getId());
        return st.stream().map(e -> talkRepo.findById(e.getTalkId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
