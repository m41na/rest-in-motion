package works.hop.scrum.repo;

import works.hop.scrum.domain.Scrum;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.ActionCreator;

import java.util.Map;
import java.util.function.Function;

public interface ScrumActions {

    String INIT_SCRUM = "INIT_SCRUM";
    String RETRIEVE_SCRUM = "RETRIEVE_SCRUM";
    String INVITE_PLAYER = "INVITE_PLAYER";
    String UPDATE_TITLE = "UPDATE_TITLE";
    String UPDATE_TASK = "UPDATE_TASK";
    String UPDATE_CHOICES = "UPDATE_CHOICES";
    String JOIN_SCRUM = "JOIN_SCRUM";
    String EXIT_SCRUM = "EXIT_SCRUM";
    String TOGGLE_LOCK = "TOGGLE_LOCK";
    String SUBMIT_VOTE = "SUBMIT_VOTE";
    String REVEAL_VOTES = "REVEAL_VOTES";
    String CLEAR_VOTES = "CLEAR_VOTES";

    ActionCreator actions = new ActionCreator();

    Function<RecordEntity, Action<Scrum>> INIT_SCRUM_ACTION = actions.create(() -> INIT_SCRUM);             //POST      /{user}                     {organizer: "", title: ""}
    Function<String, Action<Scrum>> RETRIEVE_SCRUM_ACTION = actions.create(() -> RETRIEVE_SCRUM);           //GET       /{scrumId}
    Function<PlayerJoin, Action<Scrum>> INVITE_PLAYER_ACTION = actions.create(() -> INVITE_PLAYER);       //PUT       /{scrumId}/invite           {email: "", name: ""}
    Function<TitleUpdate, Action<Scrum>> UPDATE_TITLE_ACTION = actions.create(() -> UPDATE_TITLE);          //PUT       /{scrumId}/title/{title}
    Function<TaskUpdate, Action<Scrum>> UPDATE_TASK_ACTION = actions.create(() -> UPDATE_TASK);             //PUT       /{scrumId}/task             {task: ""}
    Function<ChoicesUpdate, Action<Scrum>> UPDATE_CHOICES_ACTION = actions.create(() -> UPDATE_CHOICES);    //PUT       /{scrumId}/choices          {choices: []}
    Function<PlayerJoin, Action<Scrum>> JOIN_SCRUM_ACTION = actions.create(() -> JOIN_SCRUM);             //PUT       /{scrumId}/join             {email: "", name: ""}
    Function<PlayerExit, Action<Scrum>> EXIT_SCRUM_ACTION = actions.create(() -> EXIT_SCRUM);               //DELETE    /{scrumId}/exit/{name}
    Function<String, Action<Scrum>> TOGGLE_LOCK_ACTION = actions.create(() -> TOGGLE_LOCK);                 //PUT       /{scrumId}/lock
    Function<VoteRecord, Action<Scrum>> SUBMIT_VOTE_ACTION = actions.create(() -> SUBMIT_VOTE);             //PUT       /{scrumId}/vote            {name: "", value: ""}
    Function<String, Action<Map<String, String>>> REVEAL_VOTES_ACTION = actions.create(() -> REVEAL_VOTES); //GET       /{scrumId}/vote
    Function<String, Action<Scrum>> CLEAR_VOTES_ACTION = actions.create(() -> CLEAR_VOTES);                 //DELETE    /{scrumId}/vote
}
