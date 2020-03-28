package works.hop.withservlet.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import works.hop.withservlet.demo.entity.AttendeeTalk;

import java.util.List;

@Repository
public interface AttendeeTalkRepo extends JpaRepository<AttendeeTalk, Long> {

    List<AttendeeTalk> findAllByTalkId(Long id);

    List<AttendeeTalk> findAllByAttendeeId(Long id);
}
