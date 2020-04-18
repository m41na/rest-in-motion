package works.hop.nosql.service.impl;

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
import works.hop.nosql.command.RegisterOrganizer;
import works.hop.nosql.command.RegisterTeam;
import works.hop.nosql.config.RepoTestConfig;
import works.hop.nosql.service.VotingService;
import works.hop.scrum.entity.Team;
import works.hop.scrum.entity.User;

import java.util.Date;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepoTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class VotingServiceImplTest {

    @Autowired
    VotingService service;
    @Autowired
    private ArangoDB arangoDB;

    @Before
    public void setUp() {
        ArangoDatabase db = arangoDB.db();
        db.query("for u in users remove u in users let removed = OLD return removed", emptyMap(), BaseDocument.class);
        db.query("for t in teams remove t in teams let removed = OLD return removed", emptyMap(), BaseDocument.class);
        db.query("for s in scrums remove s in scrums let removed = OLD return removed", emptyMap(), BaseDocument.class);
        db.query("for c in chatter remove c in chatter let removed = OLD return removed", emptyMap(), BaseDocument.class);
        db.query("for v in votes remove v in votes let removed = OLD return removed", emptyMap(), BaseDocument.class);
    }

    @Test()
    public void retrieveByEmailWhichDoesNotExist() {
        AResult<User> user = service.retrieveByEmail("email");
        assertFalse(user.isOk());
        assertEquals("No entity found using provided query", user.error());
    }

    @Test
    public void retrieveByEmailWhichDoesExist() {
        RegisterOrganizer organizer = RegisterOrganizer.builder().firstName("steve").emailAddress("steve@jacks.com").dateCreated(new Date()).build();
        AResult<RegisterOrganizer> result = service.registerOrganizer(organizer);
        assertTrue(result.isOk());
        AResult<User> userByEmail = service.retrieveByEmail("steve@jacks.com");
        assertTrue(userByEmail.isOk());
        assertEquals("steve@jacks.com", userByEmail.get().getEmailAddress());
    }

    @Test
    public void registerOrganizer() {
        RegisterOrganizer organizer = new RegisterOrganizer();
        AResult<RegisterOrganizer> result = service.registerOrganizer(organizer);
        assertFalse(result.isOk());
        //add required fields
        organizer = RegisterOrganizer.builder().firstName("steve").emailAddress("steve@jacks.com").dateCreated(new Date()).build();
        AResult<RegisterOrganizer> newUser = service.registerOrganizer(organizer);
        assertTrue(newUser.isOk());
        assertNotNull(newUser.get().getId());
    }

    @Test
    public void registerTeam() {
        RegisterTeam registration = RegisterTeam.builder().build();
        AResult<RegisterTeam> result = service.registerTeam(registration);
        assertTrue(result.error().contains("date created is not an optional field"));
        //add required fields
        RegisterOrganizer organizer = RegisterOrganizer.builder().firstName("steve").emailAddress("steve@jacks.com").dateCreated(new Date()).build();
        AResult<RegisterOrganizer> registered = service.registerOrganizer(organizer);
        assertTrue(registered.isOk());
        RegisterTeam team = RegisterTeam.builder().title("bouncy").organization("workshop").organizer(organizer.getEmailAddress()).dateCreated(new Date()).build();
        AResult<RegisterTeam> newTeam = service.registerTeam(team);
        assertTrue(newTeam.isOk());
    }

    @Test
    public void retrieveTeamByIdWhichDoesNotExist() {
        AResult<Team> team = service.retrieveTeamById("NON-EXISTENT");
        assertEquals("no document was found", team.error());
    }

    @Test
    public void retrieveTeamByIdWhichDoesExist() {
        RegisterOrganizer organizer = RegisterOrganizer.builder().firstName("steve").emailAddress("steve@jacks.com").dateCreated(new Date()).build();
        AResult<RegisterOrganizer> registered = service.registerOrganizer(organizer);
        assertTrue(registered.isOk());
        RegisterTeam team = RegisterTeam.builder().title("bouncy").organization("workshop").organizer(organizer.getEmailAddress()).dateCreated(new Date()).build();
        AResult<RegisterTeam> newTeam = service.registerTeam(team);
        assertTrue(newTeam.isOk());

        AResult<Team> created = service.retrieveTeamById(newTeam.get().getId());
        assertTrue(created.isOk());
        assertEquals(newTeam.get().getId(), created.get().getId());
    }

//    @Test
//    public void retrieveTeamByNameThatExists() {
//        User user = new User(null, "steve", null, "steve@jacks.com", null, new Date());
//        RegisterOrganizer organizer = new RegisterOrganizer(user);
//        AResult<RegisterOrganizer> registered = service.registerOrganizer(organizer);
//        assertTrue(registered.isOk());
//        Team team = new Team(null, "bouncy", "workshop", registered.get().user, new Date());
//        AResult<RegisterTeam> newTeam = service.registerTeam(new RegisterTeam(team));
//        assertTrue(newTeam.isOk());
//
//        AResult<Team> created = service.retrieveTeamByName("bouncy", "workshop");
//        assertTrue(created.isOk());
//        assertEquals(newTeam.get().team.getId(), created.get().getId());
//    }
//
//    @Test
//    public void retrieveTeamsWhichDoExist() {
//        User user = new User(null, "steve", null, "steve@jacks.com", null, new Date());
//        RegisterOrganizer organizer = new RegisterOrganizer(user);
//        AResult<RegisterOrganizer> result = service.registerOrganizer(organizer);
//        assertTrue(result.isOk());
//        //add teams
//        Team team1 = new Team(null, "bouncy", "workshop", result.get().user, new Date());
//        AResult<RegisterTeam> newTeam1 = service.registerTeam(new RegisterTeam(team1));
//        assertTrue(newTeam1.isOk());
//        Team team2 = new Team(null, "sloppy", "chopping", result.get().user, new Date());
//        AResult<RegisterTeam> newTeam2 = service.registerTeam(new RegisterTeam(team2));
//        assertTrue(newTeam1.isOk());
//        //retrieve teams
//        AResult<List<Team>> teams = service.retrieveTeams(result.get().user.getId());
//        assertEquals(2, teams.get().size());
//    }
//
//    @Test
//    public void retrieveTeamsWhichDoNotExist() {
//        User user = new User(null, "steve", null, "steve@jacks.com", null, new Date());
//        RegisterOrganizer organizer = new RegisterOrganizer(user);
//        AResult<RegisterOrganizer> result = service.registerOrganizer(organizer);
//        assertTrue(result.isOk());
//        AResult<List<Team>> teams = service.retrieveTeams(result.get().user.getId());
//        assertEquals(0, teams.get().size());
//    }
//
//    @Test
//    public void createTeamChoicesWithInvalidInput() {
//        Scrum scrum = new Scrum();
//        AResult<Scrum> created = service.createTeamChoices(new CreateChoices(scrum));
//        assertFalse(created.isOk());
//    }
//
//    @Test
//    public void updateTeamChoices() {
//        User user = new User(null, "steve", null, "steve@jacks.com", null, new Date());
//        RegisterOrganizer organizer = new RegisterOrganizer(user);
//        AResult<RegisterOrganizer> result = service.registerOrganizer(organizer);
//        assertTrue(result.isOk());
//        //add teams
//        Team team = new Team(null, "bouncy", "workshop", result.get().user, new Date());
//        AResult<RegisterTeam> newTeam = service.registerTeam(new RegisterTeam(team));
//        assertTrue(newTeam.isOk());
//        //add topics
//        Scrum scrum = new Scrum(newTeam.get().team.getId(), newTeam.get().team, "nice and heavy", new String[]{"1", "2", "3", "4"}, Collections.emptySet());
//        service.createTeamChoices(new CreateChoices(scrum));
//        AResult<Scrum> newTopic = service.createTeamChoices(new CreateChoices(scrum));
//        assertTrue(newTopic.isOk());
//        //update choices
//        Scrum theScrum = newTopic.get();
//        theScrum.setChoices(new String[]{"3", "4", "5", "6", "7"});
//        AResult<Scrum> updatedTopic = service.updateTeamChoices(new UpdateChoices(theScrum));
//        assertTrue(updatedTopic.isOk());
//        assertArrayEquals(updatedTopic.get().getChoices(), new String[]{"3", "4", "5", "6", "7"});
//    }
//
//    @Test
//    public void retrieveTopic() {
//        User user = new User(null, "steve", null, "steve@jacks.com", null, new Date());
//        RegisterOrganizer organizer = new RegisterOrganizer(user);
//        AResult<RegisterOrganizer> result = service.registerOrganizer(organizer);
//        assertTrue(result.isOk());
//        //add teams
//        Team team = new Team(null, "bouncy", "workshop", result.get().user, new Date());
//        AResult<RegisterTeam> newTeam = service.registerTeam(new RegisterTeam(team));
//        assertTrue(newTeam.isOk());
//        //add topics
//        Scrum scrum = new Scrum(newTeam.get().team.getId(), newTeam.get().team, "nice and heavy", new String[]{"1", "2", "3", "4"}, Collections.emptySet());
//        service.createTeamChoices(new CreateChoices(scrum));
//        AResult<Scrum> newTopic = service.createTeamChoices(new CreateChoices(scrum));
//        //retrieve topic
//        AResult<Scrum> findTopic = service.retrieveTopic(newTeam.get().team.getId());
//        assertTrue(findTopic.isOk());
//    }

    @Test
    public void inviteParticipant() {
//        InviteParticipant participant = new InviteParticipant(1L, "jumping jacks", "benny");
//        AResult<InviteParticipant> result = service.inviteParticipant(participant);
//        assertTrue(!result.isOk());
    }

    @Test
    public void removeParticipant() {
    }

    @Test
    public void fetchParticipants() {
    }

    @Test
    public void fetchVoteChoices() {
    }

    @Test
    public void toggleVoteLock() {
    }

    @Test
    public void updateVoteChoices() {
    }

    @Test
    public void fetchVotes() {
    }

    @Test
    public void clearVotes() {
    }

    @Test
    public void closeVoting() {
    }

    @Test
    public void joinVoting() {
    }

    @Test
    public void submitVote() {
    }

    @Test
    public void sendChatMessage() {
    }
}