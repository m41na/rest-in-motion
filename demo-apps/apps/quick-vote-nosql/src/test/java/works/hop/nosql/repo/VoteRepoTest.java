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
import works.hop.scrum.entity.Vote;

import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyMap;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepoTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class VoteRepoTest {

    @Autowired
    private VoteRepo voteRepo;
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
        db.query("for v in votes remove v in votes let removed = OLD return removed", emptyMap(), BaseDocument.class);
    }

    @Test
    public void saveAndFindByParticipant() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String participant = "benny";
        String scrumName = "magic_happens_here"; //scrum name MUST NOT have whitespace
        String firstTopic = "Estimate task 123";
        Scrum scrum = new Scrum(null, newTeam.get().getId(), scrumName, firstTopic, new String[]{"1", "2", "3", "5", "8"}, new String[]{participant});
        AResult<Scrum> newScrum = scrumRepo.save(scrum);
        assertNotNull(newScrum.get().getKey());

        String topic = "raining tacos";
        Vote vote = new Vote(null, newScrum.get().getKey(), participant, topic, "8", false, new Date());
        AResult<Vote> newVote = voteRepo.save(vote);
        assertEquals(newVote.get().getKey(), String.format("%s:%s", newScrum.get().getKey(), participant));

        AResult<Vote> findVote = voteRepo.findByParticipant(newScrum.get().getKey(), participant);
        assertTrue(findVote.isOk());
    }

    @Test
    public void updateVote() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String participant = "benny";
        String scrumName = "magic_happens_here"; //scrum name MUST NOT have whitespace
        String firstTopic = "Estimate task 123";
        Scrum scrum = new Scrum(null, newTeam.get().getId(), scrumName, firstTopic, new String[]{"1", "2", "3", "5", "8"}, new String[]{participant});
        AResult<Scrum> newScrum = scrumRepo.save(scrum);
        assertNotNull(newScrum.get().getKey());

        String topic = "raining tacos";
        Vote vote = new Vote(null, newScrum.get().getKey(), participant, topic, "8", false, new Date());
        AResult<Vote> newVote = voteRepo.save(vote);
        assertEquals(String.format("%s:%s", newScrum.get().getKey(), participant), newVote.get().getKey());
        assertEquals("8", newVote.get().getValue());

        AResult<Vote> updatedVote = voteRepo.updateVote(newScrum.get().getKey(), participant, topic, "5");
        assertEquals("5", updatedVote.get().getValue());
    }

    @Test
    public void toggleVotesLock() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String participant = "benny";               //participant name MUST NOT have whitespace
        String scrumName = "magic_happens_here";    //scrum name MUST NOT have whitespace
        String firstTopic = "Estimate task 123";
        String[] choices = {"1", "2", "3", "5", "8"};
        Scrum scrum = new Scrum(null, newTeam.get().getId(), scrumName, firstTopic, choices, new String[]{participant});
        AResult<Scrum> newScrum = scrumRepo.save(scrum);
        assertNotNull(newScrum.get().getKey());

        String topic = "raining tacos";
        Vote vote = new Vote(null, newScrum.get().getKey(), participant, topic, "8", false, new Date());
        AResult<Vote> newVote = voteRepo.save(vote);
        assertEquals(String.format("%s:%s", newScrum.get().getKey(), participant), newVote.get().getKey());
        assertEquals("8", newVote.get().getValue());

        AResult<Integer> toggledVotes = voteRepo.toggleVotesLock(newScrum.get().getKey(), topic, true);
        assertEquals(1, toggledVotes.get().intValue());
    }

    @Test
    public void clearVotes() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String participant = "benny";               //participant name MUST NOT have whitespace
        String scrumName = "magic_happens_here";    //scrum name MUST NOT have whitespace
        String firstTopic = "Estimate task 123";
        String[] choices = {"1", "2", "3", "5", "8"};
        Scrum scrum = new Scrum(null, newTeam.get().getId(), scrumName, firstTopic, choices, new String[]{participant});
        AResult<Scrum> newScrum = scrumRepo.save(scrum);
        assertNotNull(newScrum.get().getKey());

        String topic = "raining tacos";
        Vote vote = new Vote(null, newScrum.get().getKey(), participant, topic, "8", false, new Date());
        AResult<Vote> newVote = voteRepo.save(vote);
        assertEquals(String.format("%s:%s", newScrum.get().getKey(), participant), newVote.get().getKey());
        assertEquals("8", newVote.get().getValue());

        AResult<Integer> clearedVotes = voteRepo.clearVotes(newScrum.get().getKey(), topic);
        assertEquals(1, clearedVotes.get().intValue());

        AResult<Vote> findVote = voteRepo.findByParticipant(newScrum.get().getKey(), participant);
        assertEquals("No document found", findVote.error());
    }

    @Test
    public void fetchCurrentVotes() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String participant = "benny";               //participant name MUST NOT have whitespace
        String scrumName = "magic_happens_here";    //scrum name MUST NOT have whitespace
        String firstTopic = "Estimate task 123";
        String[] choices = {"1", "2", "3", "5", "8"};
        Scrum scrum = new Scrum(null, newTeam.get().getId(), scrumName, firstTopic, choices, new String[]{participant});
        AResult<Scrum> newScrum = scrumRepo.save(scrum);
        assertNotNull(newScrum.get().getKey());

        String topic = "raining tacos";
        Vote vote = new Vote(null, newScrum.get().getKey(), participant, topic, "8", false, new Date());
        AResult<Vote> newVote = voteRepo.save(vote);
        assertEquals(String.format("%s:%s", newScrum.get().getKey(), participant), newVote.get().getKey());
        assertEquals("8", newVote.get().getValue());

        AResult<List<Vote>> currentVotes = voteRepo.fetchCurrentVotes(newScrum.get().getKey(), topic);
        assertEquals(1, currentVotes.get().size());
    }
}