package works.hop.reducer.scrum;

import works.hop.model.Player;
import works.hop.model.Scrum;
import works.hop.reducer.persist.JdbcState;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.state.AbstractReducer;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.State;

import java.util.*;
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
                String recordId = UUID.randomUUID().toString(); //TODO: figure out better way for generating record id
                newScrum.getKey().setRecordId(recordId);
                ((JdbcState) state).save(newScrum);
                return state.apply(recordId);
            }
            case INVITE_PLAYER: {
                PlayerRecord playerRecord = (PlayerRecord) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(playerRecord.getScrumId());
                ScrumRecord record = ScrumRecord.builder().entity(entity).build();
                Scrum scrum = record.getValue();
                List<Player> players = scrum.getPlayers() != null ? scrum.getPlayers() : new ArrayList<>();
                players.add(playerRecord.getPlayer());
                scrum.setPlayers(players);
                record.setValue(scrum);
                ((JdbcState) state).update(record.getEntity());
                return state.apply(playerRecord.getScrumId());
            }
            case UPDATE_TITLE: {
                TitleUpdate titleUpdate = (TitleUpdate) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(titleUpdate.getScrumId());
                ScrumRecord record = ScrumRecord.builder().entity(entity).build();
                Scrum scrum = record.getValue();
                scrum.setTitle(titleUpdate.getTitle());
                record.setValue(scrum);
                ((JdbcState) state).update(record.getEntity());
                return state.apply(titleUpdate.getScrumId());
            }
            case UPDATE_TASK: {
                TaskUpdate taskUpdate = (TaskUpdate) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(taskUpdate.getScrumId());
                ScrumRecord record = ScrumRecord.builder().entity(entity).build();
                Scrum scrum = record.getValue();
                scrum.setTask(taskUpdate.getTask());
                record.setValue(scrum);
                ((JdbcState) state).update(record.getEntity());
                return state.apply(taskUpdate.getScrumId());
            }
            case UPDATE_CHOICES: {
                ChoicesUpdate choicesUpdate = (ChoicesUpdate) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(choicesUpdate.getScrumId());
                ScrumRecord record = ScrumRecord.builder().entity(entity).build();
                Scrum scrum = record.getValue();
                scrum.setChoices(Arrays.asList(choicesUpdate.getChoices()));
                record.setValue(scrum);
                ((JdbcState) state).update(record.getEntity());
                return state.apply(choicesUpdate.getScrumId());
            }
            case JOIN_SCRUM: {
                PlayerRecord joining = (PlayerRecord) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(joining.getScrumId());
                ScrumRecord record = ScrumRecord.builder().entity(entity).build();
                Scrum scrum = record.getValue();
                List<Player> players = (scrum.getPlayers() != null ? scrum.getPlayers() : new ArrayList<Player>())
                        .stream().map(player -> {
                            if (player.getEmail().equalsIgnoreCase(joining.getPlayer().getEmail())) {
                                player.setJoined(true);
                            }
                            return player;
                        }).collect(Collectors.toList());
                scrum.setPlayers(players);
                record.setValue(scrum);
                ((JdbcState) state).update(record.getEntity());
                return state.apply(joining.getScrumId());
            }
            case EXIT_SCRUM: {
                ExitRecord exiting = (ExitRecord) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(exiting.getScrumId());
                ScrumRecord record = ScrumRecord.builder().entity(entity).build();
                Scrum scrum = record.getValue();
                List<Player> players = (scrum.getPlayers() != null ? scrum.getPlayers() : new ArrayList<Player>())
                        .stream().filter(player -> !player.getName().equalsIgnoreCase(exiting.getName())).collect(Collectors.toList());
                scrum.setPlayers(players);
                record.setValue(scrum);
                ((JdbcState) state).update(record.getEntity());
                return state.apply(exiting.getScrumId());
            }
            case TOGGLE_LOCK: {
                String recordId = (String) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(recordId);
                ScrumRecord record = ScrumRecord.builder().entity(entity).build();
                Scrum scrum = record.getValue();
                scrum.setLocked(scrum.getLocked() != null ? !scrum.getLocked() : true);
                record.setValue(scrum);
                ((JdbcState) state).update(record.getEntity());
                return state.apply(recordId);
            }
            case SUBMIT_VOTE: {
                VoteRecord newVote = (VoteRecord) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(newVote.getScrumId());
                ScrumRecord record = ScrumRecord.builder().entity(entity).build();
                Scrum scrum = record.getValue();
                Map<String, String> votes = (scrum.getVotes() != null ? scrum.getVotes() : new HashMap<>());
                votes.put(newVote.getVote().getName(), newVote.getVote().getValue());
                scrum.setVotes(votes);
                record.setValue(scrum);
                ((JdbcState) state).update(record.getEntity());
                return state.apply(newVote.getScrumId());
            }
            case CLEAR_VOTES: {
                String recordId = (String) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(recordId);
                ScrumRecord record = ScrumRecord.builder().entity(entity).build();
                Scrum scrum = record.getValue();
                scrum.setVotes(new HashMap<>());
                record.setValue(scrum);
                ((JdbcState) state).update(record.getEntity());
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
                RecordEntity entity = ((JdbcState) this.state()).fetch(recordId);
                ScrumRecord record = ScrumRecord.builder().entity(entity).build();
                Scrum scrum = record.getValue();
                return scrum.getVotes();
            }
            default:
                return null;
        }
    }
}