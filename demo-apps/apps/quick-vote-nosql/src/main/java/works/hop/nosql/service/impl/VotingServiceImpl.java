package works.hop.nosql.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import works.hop.nosql.command.*;
import works.hop.nosql.repo.*;
import works.hop.nosql.service.VotingService;
import works.hop.scrum.entity.Scrum;
import works.hop.scrum.entity.Team;
import works.hop.scrum.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VotingServiceImpl implements VotingService {

    private static Logger LOG = LoggerFactory.getLogger(VotingServiceImpl.class);
    private final ChatRepo chatRepo;
    private final PlayerRepo playerRepo;
    private final TeamRepo teamRepo;
    private final UserRepo userRepo;
    private final ScrumRepo scrumRepo;
    private final VoteRepo voteRepo;

    public VotingServiceImpl(@Autowired ChatRepo chatRepo,
                             @Autowired PlayerRepo playerRepo,
                             @Autowired TeamRepo teamRepo,
                             @Autowired UserRepo userRepo,
                             @Autowired ScrumRepo scrumRepo,
                             @Autowired VoteRepo voteRepo) {
        this.chatRepo = chatRepo;
        this.playerRepo = playerRepo;
        this.teamRepo = teamRepo;
        this.userRepo = userRepo;
        this.scrumRepo = scrumRepo;
        this.voteRepo = voteRepo;
    }

    @Override
    public AResult<User> retrieveByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public AResult<RegisterOrganizer> registerOrganizer(RegisterOrganizer organizer) {
        Map<String, List<String>> errors = organizer.validate();
        if (!errors.isEmpty()) {
            return AResult.empty(errors);
        }
        AResult<User> userByEmail = retrieveByEmail(organizer.getEmailAddress());
        if (userByEmail.isOk()) {
            return AResult.empty("The email address exists. Please try a different email");
        }
        AResult<User> newUser = userRepo.save(User.builder()
                .emailAddress(organizer.getEmailAddress())
                .firstName(organizer.getFirstName())
                .lastName(organizer.getLastName())
                .phoneNumber(organizer.getPhoneNumber())
                .dateCreated(Optional.ofNullable(organizer.getDateCreated()).orElse(new Date()))
                .build());
        if (newUser.get() != null) {
            organizer.setId(newUser.get().getId());
            return AResult.of(organizer);
        }
        return AResult.empty(newUser.error());
    }

    @Override
    public AResult<RegisterTeam> registerTeam(RegisterTeam registration) {
        Map<String, List<String>> errors = registration.validate();
        if (!errors.isEmpty()) {
            return AResult.empty(errors);
        }
        AResult<Team> newTeam = teamRepo.save(Team.builder()
                .title(registration.getTitle())
                .id(registration.getId())
                .organization(registration.getOrganization())
                .organizer(registration.getOrganizer())
                .dateCreated(registration.getDateCreated())
                .build());
        if (newTeam.get() != null) {
            registration.setId(newTeam.get().getId());
            return AResult.of(registration);
        }
        return AResult.empty(newTeam.error());
    }

    @Override
    public AResult<RegisterScrum> registerScrum(RegisterScrum registration) {
        Map<String, List<String>> errors = registration.validate();
        if (!errors.isEmpty()) {
            return AResult.empty(errors);
        }
        AResult<Scrum> newScrum = scrumRepo.save(Scrum.builder()
                .name(registration.getName())
                .choices(registration.getChoices())
                .participants(registration.getParticipants())
                .topic(registration.getTopic())
                .teamId(registration.getTeamId())
                .build());
        if (newScrum.get() != null) {
            registration.setKey(newScrum.get().getKey());
            return AResult.of(registration);
        }
        return AResult.empty(newScrum.error());
    }

    @Override
    public AResult<Team> retrieveTeamById(String teamKey) {
        return teamRepo.findById(teamKey);
    }

    @Override
    public AResult<Team> retrieveTeamByName(String title, String organization) {
        return teamRepo.findByName(title, organization);
    }

    @Override
    public AResult<List<Team>> retrieveTeams(String organizer) {
        AResult<List<Team>> teams = teamRepo.findByOrganizer(organizer);
        if (teams.isOk()) {
            return teams;
        }
        return AResult.empty("No teams found by criteria provided");
    }

    @Override
    public AResult<Scrum> updateScrumChoices(UpdateChoices choices) {
        AResult<Scrum> topic = scrumRepo.updateChoices(choices.scrum.getTeamId(), choices.scrum.getName(), choices.scrum.getChoices());
        if (topic.isOk()) {
            return topic;
        }
        return AResult.empty("Could not update scrum choices");
    }

    @Override
    public AResult<Scrum> retrieveScrum(String teamId, String scrumName) {
        return scrumRepo.findById(teamId, scrumName);
    }

    @Override
    public AResult<List<Scrum>> retrieveScrumsByTeam(String teamId) {
        return scrumRepo.findByTeamId(teamId);
    }

    @Override
    public AResult<Scrum> addParticipant(AddParticipant add) {
        return scrumRepo.joinScrum(add.getTeamId(), add.getScrumName(), add.getParticipant());
    }

    @Override
    public AResult<Scrum> removeParticipant(RemoveParticipant remove) {
        return scrumRepo.exitScrum(remove.getTeamId(), remove.getScrumName(), remove.getParticipant());
    }

    @Override
    public AResult<FetchVoteChoices> fetchVoteChoices(FetchVoteChoices choices) {
        return null;
    }

    @Override
    public AResult<ToggleVoteLock> toggleVoteLock(ToggleVoteLock voteLock) {
        return null;
    }

    @Override
    public AResult<FetchVotes> fetchVotes(FetchVotes reset) {
        return null;
    }

    @Override
    public AResult<ClearVotes> clearVotes(ClearVotes reset) {
        return null;
    }

    @Override
    public AResult<CloseVoting> closeVoting(CloseVoting reset) {
        return null;
    }

    @Override
    public AResult<JoinVoting> joinVoting(JoinVoting accept) {
        return null;
    }

    @Override
    public AResult<SubmitVote> submitVote(SubmitVote accept) {
        return null;
    }

    @Override
    public AResult<SendChat> sendChatMessage(SendChat accept) {
        return null;
    }
}
