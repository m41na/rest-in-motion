package works.hop.reducer.scrum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import works.hop.model.Player;
import works.hop.model.Scrum;
import works.hop.model.Vote;
import works.hop.reducer.config.PersistTestConfig;
import works.hop.reducer.persist.JdbcObserver;
import works.hop.reducer.persist.JdbcState;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.persist.RecordKey;
import works.hop.reducer.state.DefaultStore;
import works.hop.reducer.state.State;
import works.hop.reducer.state.Store;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;

import static works.hop.reducer.scrum.ScrumActions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistTestConfig.class, loader = AnnotationConfigContextLoader.class)
@Ignore("use only for integration testing")
public class ScrumReducerTest {

    final String SCRUMS_COLLECTION = "SCRUMS_COLLECTION";
    final Store store = new DefaultStore();
    final ObjectMapper mapper = new ObjectMapper();
    final Logger LOGGER = LoggerFactory.getLogger(ScrumReducerTest.class);
    @Autowired
    @Qualifier("localDS")
    DataSource dataSource;

    @Before
    public void init() {
        JdbcObserver observer = new JdbcObserver();
        State<Scrum> states = new JdbcState<>(dataSource);
        //create reducer
        store.reducer(SCRUMS_COLLECTION, new ScrumReducer<>(SCRUMS_COLLECTION, states));
        store.subscribe(SCRUMS_COLLECTION, observer);
        store.state().forEach(state -> LOGGER.info("updated state - {}", state));
    }

    @Test
    public void test_INIT_SCRUM_ACTION() throws JsonProcessingException {
        String email = "steve@email.com";
        String title = "the martian";
        String userKey = "steve";
        Scrum scrum = Scrum.builder().organizer(email).title(title).build();
        RecordEntity recordEntity = RecordEntity.builder().key(RecordKey.builder().userKey(userKey)
                .collectionKey(SCRUMS_COLLECTION).build())
                .value(mapper.writeValueAsBytes(scrum)).build();
        //dispatch reduce request
        store.dispatchAsync(INIT_SCRUM_ACTION.apply(recordEntity), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_RETRIEVE_SCRUM_ACTION() {
        String scrumId = "7b987e2c-e969-4422-b416-961ca85b439d:steve:SCRUMS_COLLECTION";
        store.dispatchQuery(RETRIEVE_SCRUM_ACTION.apply(scrumId), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_INVITE_PLAYER_ACTION() {
        String scrumId = "7b987e2c-e969-4422-b416-961ca85b439d:steve:SCRUMS_COLLECTION";
        Player player = Player.builder().email("jane@email.com").name("jane").joined(false).build();
        PlayerRecord playerEntity = PlayerRecord.builder().player(player).scrumId(scrumId).build();
        store.dispatchAsync(INVITE_PLAYER_ACTION.apply(playerEntity), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_UPDATE_TITLE_ACTION() {
        String scrumId = "7b987e2c-e969-4422-b416-961ca85b439d:steve:SCRUMS_COLLECTION";
        String title = "something awesome";
        TitleUpdate titleEntity = TitleUpdate.builder().title(title).scrumId(scrumId).build();
        store.dispatchAsync(UPDATE_TITLE_ACTION.apply(titleEntity), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_UPDATE_TASK_ACTION() {
        String scrumId = "7b987e2c-e969-4422-b416-961ca85b439d:steve:SCRUMS_COLLECTION";
        String task = "B-5544 - Handling exceptions";
        TaskUpdate taskUpdate = TaskUpdate.builder().task(task).scrumId(scrumId).build();
        store.dispatchAsync(UPDATE_TASK_ACTION.apply(taskUpdate), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_UPDATE_CHOICES_ACTION() {
        String scrumId = "7b987e2c-e969-4422-b416-961ca85b439d:steve:SCRUMS_COLLECTION";
        String[] choices = {"1", "3", "5", "7", "9"};
        ChoicesUpdate choicesUpdate = ChoicesUpdate.builder().choices(choices).scrumId(scrumId).build();
        store.dispatchAsync(UPDATE_CHOICES_ACTION.apply(choicesUpdate), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_JOIN_SCRUM_ACTION() {
        String scrumId = "7b987e2c-e969-4422-b416-961ca85b439d:steve:SCRUMS_COLLECTION";
        Player player = Player.builder().name("jane").email("jane@email.com").joined(true).build();
        PlayerRecord playerEntity = PlayerRecord.builder().player(player).scrumId(scrumId).build();
        store.dispatchAsync(JOIN_SCRUM_ACTION.apply(playerEntity), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_EXIT_SCRUM_ACTION() {
        String scrumId = "7b987e2c-e969-4422-b416-961ca85b439d:steve:SCRUMS_COLLECTION";
        String player = "jane";
        ExitRecord exitRecord = ExitRecord.builder().name(player).scrumId(scrumId).build();
        store.dispatchAsync(EXIT_SCRUM_ACTION.apply(exitRecord), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_TOGGLE_LOCK_ACTION() {
        String scrumId = "7b987e2c-e969-4422-b416-961ca85b439d:steve:SCRUMS_COLLECTION";
        store.dispatchAsync(TOGGLE_LOCK_ACTION.apply(scrumId), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_SUBMIT_VOTE_ACTION() {
        String scrumId = "7b987e2c-e969-4422-b416-961ca85b439d:steve:SCRUMS_COLLECTION";
        String player = "james";
        VoteRecord voteRecord = VoteRecord.builder().vote(Vote.builder().name(player).value("5").build()).scrumId(scrumId).build();
        store.dispatchAsync(SUBMIT_VOTE_ACTION.apply(voteRecord), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_REVEAL_VOTES_ACTION() {
        String scrumId = "c23b1851-3a9a-4ee5-be5a-f327a5ca76af:steve:SCRUMS_COLLECTION";
        store.dispatchQuery(REVEAL_VOTES_ACTION.apply(scrumId), state -> {
            Map<String, String> result = (Map<String, String>) state;
            LOGGER.info(result.toString());
        });
    }

    @Test
    public void test_CLEAR_VOTES_ACTION() {
        String scrumId = "7b987e2c-e969-4422-b416-961ca85b439d:steve:SCRUMS_COLLECTION";
        store.dispatchAsync(CLEAR_VOTES_ACTION.apply(scrumId), state -> {
            try {
                RecordEntity entity = (RecordEntity) state;
                Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                result.setResourceId(entity.getKey().getRecordId().toString());
                LOGGER.info(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testCompute() {
        System.out.println("COMPUTE test");
    }
}