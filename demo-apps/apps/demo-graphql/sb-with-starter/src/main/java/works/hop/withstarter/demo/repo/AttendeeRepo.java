package works.hop.withstarter.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import works.hop.withstarter.demo.entity.Attendee;

@Repository
public interface AttendeeRepo extends JpaRepository<Attendee, Long> {
}
