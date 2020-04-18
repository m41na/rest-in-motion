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
import works.hop.scrum.entity.Player;
import works.hop.scrum.entity.Team;
import works.hop.scrum.entity.User;

import java.util.Date;

import static java.util.Collections.emptyMap;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepoTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class PlayerRepoTest {

    @Autowired
    private TeamRepo teamRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PlayerRepo playerRepo;
    @Autowired
    private ArangoDB arangoDB;

    @Before
    public void setUp() {
        ArangoDatabase db = arangoDB.db();
        db.query("for u in users remove u in users let removed = OLD return removed", emptyMap(), BaseDocument.class);
        db.query("for t in teams remove t in teams let removed = OLD return removed", emptyMap(), BaseDocument.class);
        db.query("for p in players remove p in players let removed = OLD return removed", emptyMap(), BaseDocument.class);
    }

    @Test
    public void save() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String playerName = "benny";
        Player player = new Player(null, newTeam.get().getId(), playerName, false, new Date());
        AResult<Player> newPlayer = playerRepo.save(player);
        assertNotNull(newPlayer.get().getKey());
        assertTrue(newPlayer.get().getKey().contains(newTeam.get().getId()));
        assertTrue(newPlayer.get().getKey().contains(playerName));

        Player anotherPlayer = new Player(null, newTeam.get().getId(), playerName, false, new Date());
        AResult<Player> result = playerRepo.save(anotherPlayer);
        assertEquals("There already exists a document with the same team id and player name", result.error());
    }

    @Test
    public void findById() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String playerName = "benny";
        Player player = new Player(null, newTeam.get().getId(), playerName, false, new Date());
        AResult<Player> newPlayer = playerRepo.save(player);
        assertNotNull(newPlayer.get().getKey());

        AResult<Player> findResult = playerRepo.findById(newTeam.get().getId(), playerName);
        assertEquals("benny", findResult.get().getName());
    }

    @Test
    public void delete() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);

        Team team = new Team(null, "jumping jacks", "watson inc", newUser.get().getEmailAddress(), new Date());
        AResult<Team> newTeam = teamRepo.save(team);
        assertTrue(newTeam.get().getId() != null);

        String[] playersNames = {"benny", "jimmy", "danny"};
        for (String playerName : playersNames) {
            Player player = new Player(null, newTeam.get().getId(), playerName, false, new Date());
            AResult<Player> newPlayer = playerRepo.save(player);
            assertNotNull(newPlayer.get().getKey());
        }

        AResult<Integer> findPlayer = playerRepo.deletePlayer(newTeam.get().getId(), playersNames[2]);
        assertEquals(1, findPlayer.get().intValue());
    }
}