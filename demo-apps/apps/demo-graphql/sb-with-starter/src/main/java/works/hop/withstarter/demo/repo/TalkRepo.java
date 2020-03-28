package works.hop.withstarter.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import works.hop.withstarter.demo.entity.Talk;

@Repository
public interface TalkRepo extends JpaRepository<Talk, Long> {
}
