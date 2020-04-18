package works.hop.nosql.service;

import works.hop.nosql.command.*;
import works.hop.scrum.entity.Scrum;
import works.hop.scrum.entity.Team;
import works.hop.scrum.entity.User;

import java.util.List;

public interface VotingService {

    //******************************* ORGANIZER FUNCTIONS  **********************************/
    AResult<User> retrieveByEmail(String email);

    AResult<RegisterOrganizer> registerOrganizer(RegisterOrganizer organizer);

    AResult<RegisterTeam> registerTeam(RegisterTeam registration);

    AResult<RegisterScrum> registerScrum(RegisterScrum team);

    AResult<Team> retrieveTeamById(String teamKey);

    AResult<Team> retrieveTeamByName(String title, String organization);

    AResult<List<Team>> retrieveTeams(String organizer);

    AResult<Scrum> updateScrumChoices(UpdateChoices choices);

    AResult<Scrum> retrieveScrum(String teamId, String scrumName);

    AResult<List<Scrum>> retrieveScrumsByTeam(String teamId);

    AResult<Scrum> addParticipant(AddParticipant add);

    AResult<Scrum> removeParticipant(RemoveParticipant remove);

    AResult<FetchVoteChoices> fetchVoteChoices(FetchVoteChoices choices);

    AResult<ToggleVoteLock> toggleVoteLock(ToggleVoteLock voteLock);

    AResult<FetchVotes> fetchVotes(FetchVotes reset);

    AResult<ClearVotes> clearVotes(ClearVotes reset);

    AResult<CloseVoting> closeVoting(CloseVoting reset);

    //******************************* PARTICIPANT FUNCTIONS  **********************************/
    AResult<JoinVoting> joinVoting(JoinVoting accept);

    AResult<SubmitVote> submitVote(SubmitVote accept);

    AResult<SendChat> sendChatMessage(SendChat accept);
}
