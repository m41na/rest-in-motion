package works.hop.reducer.scrum;

import works.hop.model.Player;
import works.hop.model.Scrum;
import works.hop.reducer.persist.JdbcState;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.persist.RecordKey;
import works.hop.reducer.persist.RecordValue;
import works.hop.reducer.state.AbstractReducer;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static works.hop.reducer.scrum.ScrumActions.*;

public class ScrumReducer<S> extends AbstractReducer<S> {

    public ScrumReducer(String name, State<S> initialState) {
        super(name, initialState);
    }

    @Override
    public S reduce(State<S> state, Action action) {
        switch (action.getType().get()) {
            case INIT_SCRUM: {
                RecordEntity newScrum = (RecordEntity) action.getBody();
                String recordId = String.format("%s:%s:%s", UUID.randomUUID().toString(),
                        newScrum.getKey().getUserKey(), newScrum.getKey().getCollectionKey()); //TODO: figure out better way for generating record id
                newScrum.getKey().setRecordId(recordId);
                ((JdbcState) state).save(newScrum);
                return state.apply(recordId);
            }
            case INVITE_PLAYER: {
                String recordId = (String) action.getBody();
                RecordValue record = ((JdbcState) state).fetch(recordId);
                ((Scrum) record).getPlayers().add((Player) action.getBody());
                ((JdbcState) state).update(RecordEntity.builder().value(record).key(RecordKey.builder().recordId(recordId).build()).build());
                return state.apply(recordId);
            }
            case UPDATE_TITLE: {
                String recordId = (String) action.getBody();
                RecordValue record = ((JdbcState) state).fetch(recordId);
                ((Scrum) record).setTitle((String) action.getBody());
                ((JdbcState) state).update(RecordEntity.builder().value(record).key(RecordKey.builder().recordId(recordId).build()).build());
                return state.apply(recordId);
            }
            case UPDATE_TASK: {
                String recordId = (String) action.getBody();
                RecordValue record = ((JdbcState) state).fetch(recordId);
                ((Scrum) record).setTask((String) action.getBody());
                ((JdbcState) state).update(RecordEntity.builder().value(record).key(RecordKey.builder().recordId(recordId).build()).build());
                return state.apply(recordId);
            }
            case JOIN_SCRUM: {
                PlayerRecord newPlayer = (PlayerRecord) action.getBody();
                RecordValue record = ((JdbcState) state).fetch(newPlayer.getRecordId());
                List<Player> players = ((Scrum) record).getPlayers().stream().map(player -> {
                    if (player.getEmail().equalsIgnoreCase(newPlayer.getPlayer().getEmail())) {
                        player.setJoined(true);
                    }
                    return player;
                }).collect(Collectors.toList());
                ((Scrum) record).setPlayers(players);
                ((JdbcState) state).update(RecordEntity.builder().value(record).key(RecordKey.builder().recordId(newPlayer.getRecordId()).build()).build());
                return state.apply(newPlayer.getRecordId());
            }
            case EXIT_SCRUM: {
                ExitRecord exiting = (ExitRecord) action.getBody();
                RecordValue record = ((JdbcState) state).fetch(exiting.getRecordId());
                List<Player> players = ((Scrum) record).getPlayers().stream().filter(player -> !player.getName().equalsIgnoreCase(exiting.getName())).collect(Collectors.toList());
                ((Scrum) record).setPlayers(players);
                ((JdbcState) state).update(RecordEntity.builder().value(record).key(RecordKey.builder().recordId(exiting.getRecordId()).build()).build());
                return state.apply(exiting.getRecordId());
            }
            case TOGGLE_LOCK: {
                String recordId = (String) action.getBody();
                RecordValue record = ((JdbcState) state).fetch(recordId);
                ((Scrum) record).setLocked(!((Scrum) record).getLocked());
                ((JdbcState) state).update(RecordEntity.builder().value(record).key(RecordKey.builder().recordId(recordId).build()).build());
                return state.apply(recordId);
            }
            case SUBMIT_VOTE: {
                VoteRecord newVote = (VoteRecord) action.getBody();
                RecordValue record = ((JdbcState) state).fetch(newVote.getRecordId());
                Map<String, String> votes = ((Scrum) record).getVotes();
                votes.put(newVote.getVote().getName(), newVote.getVote().getValue());
                ((Scrum) record).setVotes(votes);
                ((JdbcState) state).update(RecordEntity.builder().value(record).key(RecordKey.builder().recordId(newVote.getRecordId()).build()).build());
                return state.apply(newVote.getRecordId());
            }
            case CLEAR_VOTES: {
                String recordId = (String) action.getBody();
                RecordValue record = ((JdbcState) state).fetch(recordId);
                ((Scrum) record).setVotes(new HashMap<>());
                ((JdbcState) state).update(RecordEntity.builder().value(record).key(RecordKey.builder().recordId(recordId).build()).build());
                return state.apply(recordId);
            }
            default:
                return null;
        }
    }

    @Override
    public Object compute(Action action) {
        switch (action.getType().get()) {
            case RETRIEVE_SCRUM: {
                String recordId = (String) action.getBody();
                return this.state().apply(recordId);
            }
            case REVEAL_VOTES: {
                String recordId = (String) action.getBody();
                RecordValue record = ((JdbcState) this.state()).fetch(recordId);
                return ((Scrum) record).getVotes();
            }
            default:
                return null;
        }
    }
}