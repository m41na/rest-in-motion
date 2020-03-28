package works.hop.withcontroller.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import works.hop.withcontroller.demo.entity.Speaker;

@Repository
public interface SpeakerRepo extends JpaRepository<Speaker, Long> {
}
