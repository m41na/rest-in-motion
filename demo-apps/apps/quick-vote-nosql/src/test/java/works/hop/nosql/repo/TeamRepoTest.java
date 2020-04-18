package works.hop.nosql.repo;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import works.hop.nosql.command.AResult;
import works.hop.nosql.config.RepoTestConfig;
import works.hop.scrum.entity.Team;
import works.hop.scrum.entity.User;

import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepoTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class TeamRepoTest {

    @Autowired
    private TeamRepo teamRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ArangoDB arangoDB;

    @Before
    public void setUp() {
        ArangoDatabase db = arangoDB.db();
        db.query("for u in users remove u in users let removed = OLD return removed", emptyMap(), BaseDocument.class);
        db.query("for t in teams remove t in teams let removed = OLD return removed", emptyMap(), BaseDocument.class);
    }

    @Test
    public void save() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertNotNull(newUser.get().getId());

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);
        assertEquals(newTeam.get().getOrganizer(), newUser.get().getEmailAddress());
    }

    @Test
    public void merge() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertNotNull(newUser.get().getId());

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);
        assertEquals(newTeam.get().getOrganizer(), newUser.get().getEmailAddress());

        Team target = newTeam.get();
        target.setOrganization("bella ciao");
        Team updated = teamRepo.merge(target).get();
        assertEquals("bella ciao", target.getOrganization());
        assertEquals(newTeam.get().getOrganizer(), updated.getOrganizer());
    }

    @Test
    public void saveWithoutUniqueTitleAndOrganization() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertNotNull(newUser.get().getId());

        Team team1 = new Team(null, "jumping jacks", "watson inc test", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newResult1 = teamRepo.save(team1);
        assertTrue(newResult1.get() != null);

        Team team2 = new Team(null, "jumping jacks", "watson inc test", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newResult = teamRepo.save(team2);
        assertEquals("A team already exists with the same title and organization", newResult.error());
    }

    @Test
    public void findById() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertNotNull(newUser.get().getId());

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get() != null);

        AResult<Team> findResult = teamRepo.findById(newTeam.get().getId());
        assertEquals(findResult.get().getId(), newTeam.get().getId());
    }

    @Test
    public void findByName() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> result = userRepo.save(user);
        assertNotNull(result.get().getId());

        Team team = new Team(null, "jumping jacks", "watson inc", result.get().getEmailAddress(), new Date());
        AResult<Team> newResult = teamRepo.save(team);
        assertTrue(newResult.get() != null);

        AResult<Team> findResult = teamRepo.findByName("jumping jacks", "watson inc");
        assertEquals("jumping jacks", findResult.get().getTitle());
    }

    @Test
    public void findByOrganizer() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> result = userRepo.save(user);
        assertNotNull(result.get().getId());

        Team team = new Team(null, "jumping jacks", "watson inc", result.get().getEmailAddress(), new Date());
        AResult<Team> newResult = teamRepo.save(team);
        assertTrue(newResult.get() != null);

        AResult<List<Team>> findResult = teamRepo.findByOrganizer(newResult.get().getOrganizer());
        assertEquals(1, findResult.get().size());
        assertEquals("jumping jacks", findResult.get().get(0).getTitle());
    }
}