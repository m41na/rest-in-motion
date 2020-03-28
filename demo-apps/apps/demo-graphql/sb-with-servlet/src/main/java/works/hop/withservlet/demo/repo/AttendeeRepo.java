package works.hop.withservlet.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import works.hop.withservlet.demo.entity.Attendee;

@Repository
public interface AttendeeRepo extends JpaRepository<Attendee, Long> {
}
