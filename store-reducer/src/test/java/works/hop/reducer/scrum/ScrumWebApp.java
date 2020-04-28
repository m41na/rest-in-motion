package works.hop.reducer.scrum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import works.hop.core.JsonResult;
import works.hop.model.Organizer;
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
import java.util.UUID;

import static java.util.Collections.emptyMap;
import static works.hop.jetty.JettyStartable.createServer;
import static works.hop.jetty.startup.AppOptions.applyDefaults;
import static works.hop.reducer.scrum.ScrumActions.*;

public class ScrumWebApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrumWebApp.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(PersistTestConfig.class);
        DataSource remoteDataSource = ctx.getBean("remoteDS", DataSource.class);
        DataSource dataSource = ctx.getBean("localDS", DataSource.class);
        ObjectMapper mapper = new ObjectMapper();

        final String SCRUMS_COLLECTION = "SCRUMS_COLLECTION";
        JdbcObserver observer = new JdbcObserver();
        State<Scrum> states = new JdbcState<>(dataSource);

        //create reducer
        Store store = new DefaultStore();
        store.reducer(SCRUMS_COLLECTION, new ScrumReducer<>(SCRUMS_COLLECTION, states));
        store.subscribe(SCRUMS_COLLECTION, observer);
        store.state().forEach(state -> LOGGER.info("updated state - {}", state));

        //create rest api
        Map<String, String> properties = applyDefaults(new Options(), args);
        var app = createServer(properties.get("appctx"), properties, builder -> builder.cors(emptyMap())
                .sse("/sse", (config) -> config.setAsyncSupported(true), observer).build());
        app.before((req, res, done) -> System.out.println("PRINT BEFORE ALL /"));
        app.before("get", "/", (req, res, done) -> System.out.println("PRINT BEFORE GET /"));
        app.get("/", (req, res, done) -> done.resolve(() -> {
            res.send("SCRUM web app using jdbc reducer");
        }));
        app.post("/{userKey}", "application/json", "application/json", (req, res, done) -> {
            String userKey = req.param("userKey");
            Organizer organizer = req.body(Organizer.class);
            Scrum scrum = Scrum.builder().organizer(organizer.getEmail()).title(organizer.getTitle()).resourceId(UUID.randomUUID().toString()).build();
            try {
                RecordEntity recordEntity = RecordEntity.builder().key(RecordKey.builder().userKey(userKey)
                        .collectionKey(SCRUMS_COLLECTION).build())
                        .value(mapper.writeValueAsBytes(scrum)).build();
                done.resolve(store.dispatchAsync(INIT_SCRUM_ACTION.apply(recordEntity), state -> {
                    try {
                        RecordEntity entity = (RecordEntity) state;
                        Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                        result.setResourceId(entity.getKey().getRecordId().toString());
                        res.json(JsonResult.ok(result));
                    } catch (IOException e) {
                        res.json(JsonResult.err(e.getMessage()));
                    }
                }));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        app.get("/{scrumId}", (req, res, done) -> {
            String scrumId = req.param("scrumId");
            done.resolve(store.dispatchQuery(RETRIEVE_SCRUM_ACTION.apply(scrumId), state -> {
                try {
                    RecordEntity entity = (RecordEntity) state;
                    Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                    result.setResourceId(entity.getKey().getRecordId().toString());
                    res.json(JsonResult.ok(result));
                } catch (IOException e) {
                    res.json(JsonResult.err(e.getMessage()));
                }
            }));
        });
        app.put("/{scrumId}/invite", "application/json", "application/json", (req, res, done) -> {
            String scrumId = req.param("scrumId");
            Player player = req.body(Player.class);
            PlayerRecord playerEntity = PlayerRecord.builder().player(player).scrumId(scrumId).build();
            done.resolve(store.dispatchAsync(INVITE_PLAYER_ACTION.apply(playerEntity), state -> {
                try {
                    RecordEntity entity = (RecordEntity) state;
                    Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                    result.setResourceId(entity.getKey().getRecordId().toString());
                    res.json(JsonResult.ok(result));
                } catch (IOException e) {
                    res.json(JsonResult.err(e.getMessage()));
                }
            }));
        });
        app.put("/{scrumId}/title/{title}", "application/json", "application/json", (req, res, done) -> {
            String scrumId = req.param("scrumId");
            String title = req.param("title");
            TitleUpdate titleEntity = TitleUpdate.builder().title(title).scrumId(scrumId).build();
            done.resolve(store.dispatchAsync(UPDATE_TITLE_ACTION.apply(titleEntity), state -> {
                try {
                    RecordEntity entity = (RecordEntity) state;
                    Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                    result.setResourceId(entity.getKey().getRecordId().toString());
                    res.json(JsonResult.ok(result));
                } catch (IOException e) {
                    res.json(JsonResult.err(e.getMessage()));
                }
            }));
        });
        app.put("/{scrumId}/task/{task}", "application/json", "application/json", (req, res, done) -> {
            String scrumId = req.param("scrumId");
            String task = req.param("task");
            TaskUpdate taskEntity = TaskUpdate.builder().task(task).scrumId(scrumId).build();
            done.resolve(store.dispatchAsync(UPDATE_TASK_ACTION.apply(taskEntity), state -> {
                try {
                    RecordEntity entity = (RecordEntity) state;
                    Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                    result.setResourceId(entity.getKey().getRecordId().toString());
                    res.json(JsonResult.ok(result));
                } catch (IOException e) {
                    res.json(JsonResult.err(e.getMessage()));
                }
            }));
        });
        app.put("/{scrumId}/choices", "application/json", "application/json", (req, res, done) -> {
            String scrumId = req.param("scrumId");
            String[] choices = req.body(String[].class);
            ChoicesUpdate choicesUpdate = ChoicesUpdate.builder().choices(choices).scrumId(scrumId).build();
            done.resolve(store.dispatchAsync(UPDATE_CHOICES_ACTION.apply(choicesUpdate), state -> {
                try {
                    RecordEntity entity = (RecordEntity) state;
                    Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                    result.setResourceId(entity.getKey().getRecordId().toString());
                    res.json(JsonResult.ok(result));
                } catch (IOException e) {
                    res.json(JsonResult.err(e.getMessage()));
                }
            }));
        });
        app.put("/{scrumId}/join", "application/json", "application/json", (req, res, done) -> {
            String scrumId = req.param("scrumId");
            Player player = req.body(Player.class);
            PlayerRecord playerEntity = PlayerRecord.builder().player(player).scrumId(scrumId).build();
            done.resolve(store.dispatchAsync(JOIN_SCRUM_ACTION.apply(playerEntity), state -> {
                try {
                    RecordEntity entity = (RecordEntity) state;
                    Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                    result.setResourceId(entity.getKey().getRecordId().toString());
                    res.json(JsonResult.ok(result));
                } catch (IOException e) {
                    res.json(JsonResult.err(e.getMessage()));
                }
            }));
        });
        app.put("/{scrumId}/exit/{name}", "application/json", "application/json", (req, res, done) -> {
            String scrumId = req.param("scrumId");
            String player = req.param("name");
            ExitRecord exitEntity = ExitRecord.builder().name(player).scrumId(scrumId).build();
            done.resolve(store.dispatchAsync(EXIT_SCRUM_ACTION.apply(exitEntity), state -> {
                try {
                    RecordEntity entity = (RecordEntity) state;
                    Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                    result.setResourceId(entity.getKey().getRecordId().toString());
                    res.json(JsonResult.ok(result));
                } catch (IOException e) {
                    res.json(JsonResult.err(e.getMessage()));
                }
            }));
        });
        app.put("/{scrumId}/lock", "application/json", "application/json", (req, res, done) -> {
            String scrumId = req.param("scrumId");
            done.resolve(store.dispatchAsync(TOGGLE_LOCK_ACTION.apply(scrumId), state -> {
                try {
                    RecordEntity entity = (RecordEntity) state;
                    Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                    result.setResourceId(entity.getKey().getRecordId().toString());
                    res.json(JsonResult.ok(result));
                } catch (IOException e) {
                    res.json(JsonResult.err(e.getMessage()));
                }
            }));
        });
        app.put("/{scrumId}/vote", "application/json", "application/json", (req, res, done) -> {
            String scrumId = req.param("scrumId");
            Vote vote = req.body(Vote.class);
            VoteRecord voteRecord = VoteRecord.builder().vote(vote).scrumId(scrumId).build();
            done.resolve(store.dispatchAsync(SUBMIT_VOTE_ACTION.apply(voteRecord), state -> {
                try {
                    RecordEntity entity = (RecordEntity) state;
                    Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                    result.setResourceId(entity.getKey().getRecordId().toString());
                    res.json(JsonResult.ok(result));
                } catch (IOException e) {
                    res.json(JsonResult.err(e.getMessage()));
                }
            }));
        });
        app.get("/{scrumId}/vote", (req, res, done) -> {
            String scrumId = req.param("scrumId");
            done.resolve(store.dispatchQuery(REVEAL_VOTES_ACTION.apply(scrumId), state -> {
                Map<String, String> result = (Map<String, String>) state;
                res.json(JsonResult.ok(result));
            }));
        });
        app.delete("/{scrumId}/vote", (req, res, done) -> {
            String scrumId = req.param("scrumId");
            done.resolve(store.dispatchAsync(CLEAR_VOTES_ACTION.apply(scrumId), state -> {
                try {
                    RecordEntity entity = (RecordEntity) state;
                    Scrum result = mapper.readValue(entity.getValue(), Scrum.class);
                    result.setResourceId(entity.getKey().getRecordId().toString());
                    res.json(JsonResult.ok(result));
                } catch (IOException e) {
                    res.json(JsonResult.err(e.getMessage()));
                }
            }));
        });
        app.after("get", "/", (req, res, done) -> System.out.println("PRINT AFTER GET /"));
        app.after((req, res, done) -> System.out.println("PRINT AFTER ALL /"));
        app.listen(8090, "localhost");
    }
}
