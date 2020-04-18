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
import works.hop.scrum.entity.Scrum;
import works.hop.scrum.entity.Team;
import works.hop.scrum.entity.User;

import java.util.Date;

import static java.util.Collections.emptyMap;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepoTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class ScrumRepoTest {

    @Autowired
    private TeamRepo teamRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ScrumRepo scrumRepo;
    @Autowired
    private ArangoDB arangoDB;

    @Before
    public void setUp() {
        ArangoDatabase db = arangoDB.db();
        db.query("for u in users remove u in users let removed = OLD return removed", emptyMap(), BaseDocument.class);
        db.query("for t in teams remove t in teams let removed = OLD return removed", emptyMap(), BaseDocument.class);
        db.query("for s in scrums remove s in scrums let removed = OLD return removed", emptyMap(), BaseDocument.class);
    }

    @Test
    public void save() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String scrumName = "magic_happens_here"; //scrum name MUST NOT have whitespace
        String firstTopic = "Estimate task 123";
        Scrum scrum = new Scrum(null, newTeam.get().getId(), scrumName, firstTopic, new String[]{"1", "2", "3", "5", "8"}, new String[]{});
        AResult<Scrum> newScrum = scrumRepo.save(scrum);
        assertNotNull(newScrum.get().getKey());
        assertTrue(newScrum.get().getKey().contains(newTeam.get().getId()));
        assertTrue(newScrum.get().getKey().contains(scrumName));
    }

    @Test
    public void updateTopic() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String scrumName = "magic_happens_here"; //scrum name MUST NOT have whitespace
        String firstTopic = "Estimate task 123";
        Scrum scrum = new Scrum(null, newTeam.get().getId(), scrumName, firstTopic, new String[]{"1", "2", "3", "5", "8"}, new String[]{});
        AResult<Scrum> newScrum = scrumRepo.save(scrum);
        assertNotNull(newScrum.get().getKey());

        String newTopic = "Estimating task 456";
        AResult<Scrum> updatedScrum = scrumRepo.updateTopic(scrum.getTeamId(), scrum.getName(), newTopic);
        assertNotNull(updatedScrum.get().getKey());
        assertEquals(newTopic, updatedScrum.get().getTopic());
    }

    @Test
    public void updateChoices() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String scrumName = "magic_happens_here"; //scrum name MUST NOT have whitespace
        String firstTopic = "Estimate task 123";
        Scrum scrum = new Scrum(null, newTeam.get().getId(), scrumName, firstTopic, new String[]{"1", "2", "3", "5", "8"}, new String[]{});
        AResult<Scrum> newScrum = scrumRepo.save(scrum);
        assertNotNull(newScrum.get().getKey());

        String[] newChoices = {"z", "x", "c", "v", "b"};
        AResult<Scrum> updatedScrum = scrumRepo.updateChoices(scrum.getTeamId(), scrum.getName(), newChoices);
        assertNotNull(updatedScrum.get().getKey());
        assertArrayEquals(newChoices, updatedScrum.get().getChoices());
    }

    @Test
    public void joinScrum() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String scrumName = "magic_happens_here"; //scrum name MUST NOT have whitespace
        String firstTopic = "Estimate task 123";
        Scrum scrum = new Scrum(null, newTeam.get().getId(), scrumName, firstTopic, new String[]{"1", "2", "3", "5", "8"}, new String[]{});
        AResult<Scrum> newScrum = scrumRepo.save(scrum);
        assertNotNull(newScrum.get().getKey());

        String firstParticipant = "jacobsen";
        AResult<Scrum> afterJoining = scrumRepo.joinScrum(newTeam.get().getId(), scrumName, firstParticipant);
        assertEquals(1, afterJoining.get().getParticipants().length);
    }

    @Test
    public void exitScrum() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String scrumName = "magic_happens_here"; //scrum name MUST NOT have whitespace
        String firstTopic = "Estimate task 123";
        Scrum scrum = new Scrum(null, newTeam.get().getId(), scrumName, firstTopic, new String[]{"1", "2", "3", "5", "8"}, new String[]{});
        AResult<Scrum> newScrum = scrumRepo.save(scrum);
        assertNotNull(newScrum.get().getKey());

        String participants = "jacobsen,erastus,paulina,justice";
        AResult<Scrum> afterJoining = scrumRepo.joinScrum(newTeam.get().getId(), scrumName, participants);
        assertEquals(4, afterJoining.get().getParticipants().length);

        AResult<Scrum> afterExiting = scrumRepo.exitScrum(newTeam.get().getId(), scrumName, "erastus");
        assertEquals(3, afterExiting.get().getParticipants().length);
    }
}