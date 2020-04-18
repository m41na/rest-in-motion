package works.hop.nosql.command;

import com.arangodb.entity.BaseDocument;
import works.hop.scrum.entity.*;

import java.util.Date;
import java.util.Optional;

public class EntityDocuments {

    public static BaseDocument fromUser(User user) {
        BaseDocument doc = new BaseDocument();
        doc.setKey(user.getId());
        doc.addAttribute("id", user.getId());
        doc.addAttribute("firstName", user.getFirstName());
        doc.addAttribute("lastName", user.getLastName());
        doc.addAttribute("emailAddress", user.getEmailAddress());
        doc.addAttribute("phoneNumber", user.getPhoneNumber());
        doc.addAttribute("dateCreated", user.getDateCreated().getTime());
        return doc;
    }

    public static User toUser(BaseDocument doc) {
        User user = User.builder()
                .id(Optional.ofNullable(doc.getKey()).orElse(""))
                .firstName(Optional.ofNullable(doc.getAttribute("firstName")).orElse("").toString())
                .lastName(Optional.ofNullable(doc.getAttribute("lastName")).orElse("").toString())
                .emailAddress(Optional.ofNullable(doc.getAttribute("emailAddress")).orElse("").toString())
                .phoneNumber(Optional.ofNullable(doc.getAttribute("phoneNumber")).orElse("").toString())
                .dateCreated(new Date(Long.valueOf(Optional.ofNullable(doc.getAttribute("dateCreated").toString()).orElse(""))))
                .build();
        return user;
    }

    public static BaseDocument fromVote(Vote vote) {
        BaseDocument doc = new BaseDocument();
        doc.setKey(vote.getScrumKey() + ":" + vote.getParticipant());
        doc.addAttribute("scrumKey", vote.getScrumKey());
        doc.addAttribute("participant", vote.getParticipant());
        doc.addAttribute("value", vote.getValue());
        doc.addAttribute("locked", vote.getLocked());
        doc.addAttribute("topic", vote.getTopic());
        doc.addAttribute("timeVoted", vote.getTimeVoted().getTime());
        return doc;
    }

    public static Vote toVote(BaseDocument doc) {
        Vote vote = Vote.builder()
                .key(Optional.ofNullable(doc.getKey()).orElse(""))
                .scrumKey(Optional.ofNullable(doc.getAttribute("scrumKey")).orElse("").toString())
                .participant(Optional.ofNullable(doc.getAttribute("participant")).orElse("").toString())
                .value(Optional.ofNullable(doc.getAttribute("value")).orElse("").toString())
                .locked(Boolean.parseBoolean(Optional.ofNullable(doc.getAttribute("locked")).orElse("false").toString()))
                .topic(Optional.ofNullable(doc.getAttribute("topic")).orElse("").toString())
                .timeVoted(new Date(Long.valueOf(Optional.ofNullable(doc.getAttribute("timeVoted")).orElse("").toString())))
                .build();
        return vote;
    }

    public static BaseDocument fromChat(Chat chat) {
        BaseDocument doc = new BaseDocument();
        doc.setKey(chat.getScrumKey() + ":" + chat.getSender() + ":" + chat.getDateSent().getTime());
        doc.addAttribute("scrumKey", chat.getScrumKey());
        doc.addAttribute("sender", chat.getSender());
        doc.addAttribute("message", chat.getMessage());
        doc.addAttribute("dateSent", chat.getDateSent().getTime());
        doc.addAttribute("subject", chat.getSubject());
        doc.addAttribute("recipients", String.join(",", chat.getRecipients()));
        return doc;
    }

    public static Chat toChat(BaseDocument doc) {
        Chat chat = Chat.builder()
                .key(Optional.ofNullable(doc.getKey()).orElse(""))
                .scrumKey(Optional.ofNullable(doc.getAttribute("scrumKey")).orElse("0").toString())
                .sender(Optional.ofNullable(doc.getAttribute("sender")).orElse("").toString())
                .message(Optional.ofNullable(doc.getAttribute("message")).orElse("").toString())
                .subject(Optional.ofNullable(doc.getAttribute("locked")).orElse("").toString())
                .recipients(Optional.ofNullable(doc.getAttribute("topic")).orElse("").toString().split(","))
                .dateSent(new Date(Long.valueOf(Optional.ofNullable(doc.getAttribute("dateSent")).orElse("").toString())))
                .build();
        return chat;
    }

    public static BaseDocument fromPlayer(Player player) {
        BaseDocument doc = new BaseDocument();
        doc.setKey(player.getTeamId() + ":" + player.getName());
        doc.addAttribute("teamId", player.getTeamId());
        doc.addAttribute("name", player.getName());
        doc.addAttribute("active", player.getActive());
        doc.addAttribute("lastJoined", player.getLastJoined().getTime());
        return doc;
    }

    public static Player toPlayer(BaseDocument doc) {
        Player player = Player.builder()
                .key(Optional.ofNullable(doc.getKey()).orElse(""))
                .teamId(Optional.ofNullable(doc.getAttribute("scrumId")).orElse("").toString())
                .name(Optional.ofNullable(doc.getAttribute("name")).orElse("").toString())
                .active(Boolean.parseBoolean(Optional.ofNullable(doc.getAttribute("active")).orElse("true").toString()))
                .lastJoined(new Date(Long.valueOf(Optional.ofNullable(doc.getAttribute("lastJoined")).orElse("").toString())))
                .build();
        return player;
    }

    public static BaseDocument fromScrum(Scrum scrum) {
        BaseDocument doc = new BaseDocument();
        doc.setKey(scrum.getTeamId() + ":" + scrum.getName());
        doc.addAttribute("teamId", scrum.getTeamId());
        doc.addAttribute("name", scrum.getName());
        doc.addAttribute("topic", scrum.getTopic());
        doc.addAttribute("participants", String.join(",", scrum.getParticipants()));
        doc.addAttribute("choices", String.join(",", scrum.getChoices()));
        return doc;
    }

    public static Scrum toScrum(BaseDocument doc) {
        Scrum scrum = Scrum.builder()
                .key(Optional.ofNullable(doc.getKey()).orElse(""))
                .teamId(Optional.ofNullable(doc.getAttribute("teamId")).orElse("").toString())
                .name(Optional.ofNullable(doc.getAttribute("name")).orElse("").toString())
                .topic(Optional.ofNullable(doc.getAttribute("topic")).orElse("").toString())
                .participants(Optional.ofNullable(doc.getAttribute("participants")).orElse("").toString().split(","))
                .choices(Optional.ofNullable(doc.getAttribute("choices")).orElse("").toString().split(","))
                .build();
        return scrum;
    }

    public static BaseDocument fromTeam(Team team) {
        BaseDocument doc = new BaseDocument();
        doc.addAttribute("id", team.getId());
        doc.addAttribute("title", team.getTitle());
        doc.addAttribute("organization", team.getOrganization());
        doc.addAttribute("organizer", team.getOrganizer());
        doc.addAttribute("dateCreated", team.getDateCreated().getTime());
        return doc;
    }

    public static Team toTeam(BaseDocument doc) {
        Team team = Team.builder()
                .id(Optional.ofNullable(doc.getKey()).orElse(""))
                .title(Optional.ofNullable(doc.getAttribute("title")).orElse("").toString())
                .organization(Optional.ofNullable(doc.getAttribute("organization")).orElse("").toString())
                .organizer(Optional.ofNullable(doc.getAttribute("organizer")).orElse("").toString())
                .dateCreated(new Date(Long.valueOf(Optional.ofNullable(doc.getAttribute("dateCreated")).orElse("").toString())))
                .build();
        return team;
    }
}
