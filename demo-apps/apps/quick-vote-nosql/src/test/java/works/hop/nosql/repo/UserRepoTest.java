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
import works.hop.scrum.entity.User;

import java.util.Date;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepoTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class UserRepoTest {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ArangoDB arangoDB;

    @Before
    public void setUp() {
        ArangoDatabase db = arangoDB.db();
        db.query("for u in users remove u in users let removed = OLD return removed", emptyMap(), BaseDocument.class);
    }

    @Test
    public void register() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> newUser = userRepo.save(user);
        assertTrue(newUser.get() != null);
        assertNotNull(newUser.get().getId());
        AResult<User> byId = userRepo.findById(newUser.get().getId());
        assertEquals(byId.get().getId(), newUser.get().getId());
    }

    @Test
    public void update() {
        User user = new User(null, "maggie", "jonson", "maggie@email.com", null, new Date());
        AResult<User> result = userRepo.save(user);

        User target = result.get();
        target.setPhoneNumber("608-998-7654");
        User updated = userRepo.merge(target).get();
        assertEquals("maggie", target.getFirstName());
        assertEquals("608-998-7654", updated.getPhoneNumber());
    }

    @Test
    public void findById() {
        User user = new User(null, "james", "watson", "james@email.com", null, new Date());
        AResult<User> result = userRepo.save(user);
        User found = userRepo.findById(result.get().getId()).get();
        assertEquals(result.get().getId(), found.getId());
    }

    @Test
    public void findByEmail() {
        User james = new User(null, "peter", "watson", "peter@email.com", null, new Date());
        AResult<User> jamesResult = userRepo.save(james);

        User nancy = new User(null, "nancy", "watson", "nancy@email.com", null, new Date());
        AResult<User> nancyResult = userRepo.save(nancy);

        AResult<User> found = userRepo.findByEmail("peter@email.com");
        assertEquals(found.get().getEmailAddress(), james.getEmailAddress());
    }
}
