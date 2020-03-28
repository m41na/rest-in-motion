package works.hop.withcontroller.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import works.hop.withcontroller.demo.entity.SpeakerTalk;

import java.util.List;

@Repository
public interface SpeakerTalkRepo extends JpaRepository<SpeakerTalk, Long> {

    List<SpeakerTalk> findAllByTalkId(Long id);

    List<SpeakerTalk> findAllBySpeakerId(Long id);
}
