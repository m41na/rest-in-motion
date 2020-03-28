package works.hop.withcontroller.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import works.hop.withcontroller.demo.entity.Attendee;
import works.hop.withcontroller.demo.entity.AttendeeTalk;
import works.hop.withcontroller.demo.entity.Talk;
import works.hop.withcontroller.demo.repo.AttendeeRepo;
import works.hop.withcontroller.demo.repo.AttendeeTalkRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendeeService {

    @Autowired
    private AttendeeRepo attendeeRepo;
    @Autowired
    private AttendeeTalkRepo attendeeTalkRepo;

    public List<Attendee> findAll() {
        return attendeeRepo.findAll();
    }

    public List<Attendee> findAllAttendeesForTalk(Talk talk) {
        List<AttendeeTalk> at = attendeeTalkRepo.findAllByTalkId(talk.getId());
        return at.stream().map(e -> attendeeRepo.findById(e.getAttendeeId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Attendee save(Attendee attendee) {
        return attendeeRepo.save(attendee);
    }
}
