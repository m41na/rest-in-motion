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
import works.hop.scrum.entity.Chat;
import works.hop.scrum.entity.Scrum;
import works.hop.scrum.entity.Team;
import works.hop.scrum.entity.User;

import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyMap;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepoTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class ChatRepoTest {

    @Autowired
    private ChatRepo chatRepo;
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
        db.query("for c in chatter remove c in chatter let removed = OLD return removed", emptyMap(), BaseDocument.class);
    }

    @Test
    public void save() {
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

        String sender = "mikey";
        Chat chat = new Chat(null, newScrum.get().getKey(), sender, new String[]{"mikes"}, "Are we there yet?", "first message", new Date());
        AResult<Chat> newChat = chatRepo.save(chat);
        assertEquals(newChat.get().getKey(), String.format("%s:%s:%d", newScrum.get().getKey(), sender, chat.getDateSent().getTime()));
    }

    @Test
    public void retrieveConversationAndThenClear() {
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

        String[] recipients = {"mikey", "tembo", "mamba"};
        String sender1 = "mikey";
        Chat chat1 = new Chat(null, newScrum.get().getKey(), sender1, recipients, "Are we there yet?", "first message", new Date());
        AResult<Chat> newChat1 = chatRepo.save(chat1);
        assertEquals(newChat1.get().getKey(), String.format("%s:%s:%d", newScrum.get().getKey(), sender1, chat1.getDateSent().getTime()));

        String sender2 = "tembo";
        Chat chat2 = new Chat(null, newScrum.get().getKey(), sender2, recipients, "Are we there yet?", "I think were are", new Date());
        AResult<Chat> newChat2 = chatRepo.save(chat2);
        assertEquals(newChat2.get().getKey(), String.format("%s:%s:%d", newScrum.get().getKey(), sender2, chat2.getDateSent().getTime()));

        String sender = "mamba";
        Chat chat3 = new Chat(null, newScrum.get().getKey(), sender, recipients, "Are we there yet?", "Nope, not yet!!", new Date());
        AResult<Chat> newChat3 = chatRepo.save(chat3);
        assertEquals(newChat3.get().getKey(), String.format("%s:%s:%d", newScrum.get().getKey(), sender, chat3.getDateSent().getTime()));

        Chat chat4 = new Chat(null, newScrum.get().getKey(), sender, recipients, "Are we there yet?", "Actually, yes!", new Date());
        AResult<Chat> newChat4 = chatRepo.save(chat4);
        assertEquals(newChat4.get().getKey(), String.format("%s:%s:%d", newScrum.get().getKey(), sender, chat4.getDateSent().getTime()));

        AResult<List<Chat>> conversation = chatRepo.retrieveConversation(newScrum.get().getKey());
        assertEquals(4, conversation.get().size());

        AResult<Integer> clearedChat = chatRepo.clearChat(newScrum.get().getKey());
        assertEquals(4, clearedChat.get().intValue());
    }
}