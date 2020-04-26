package works.hop.model.avro;

import works.hop.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class AConverter {

    public static Todo from(ATodo source) {
        return Todo.builder().completed(source.getCompleted())
                .id(source.getId())
                .task(source.getTask().toString())
                .build();
    }

    public static TodoList from(TaskList source) {
        return TodoList.builder().list(source.getList().stream()
                .map(lists -> ((List<Task>) lists).stream()
                        .map(task -> Todo.builder().task(task.getTask().toString()).id(task.getId()).completed(task.getCompleted()).build())
                        .collect(Collectors.toList()))
                .collect(Collectors.toList())).build();
    }

    public static Profile from(AProfile source) {
        return Profile.builder()
                .booleans(source.getBooleans())
                .floats(source.getFloats())
                .integers(source.getIntegers())
                .strings(source.getStrings().stream().map(seq -> seq.toString()).collect(Collectors.toList()))
                .phoneNumbers(source.getPhoneNumbers().stream().map(ph -> {
                    APhone phone = (APhone) ph;
                    return Phone.builder()
                            .number(phone.getNumber().toString()).type(phone.getType().toString()).build();
                }).collect(Collectors.toList()))
                .firstName(source.getFirstName().toString())
                .lastName(source.getLastName().toString())
                .age(source.getAge())
                .height(source.getHeight())
                .registered(source.getRegistered())
                .build();
    }

    public static Scrum from(AScrum source) {
        return Scrum.builder()
                .choices(source.getChoices().stream().map(ch -> ch.toString()).collect(Collectors.toList()))
                .votes(source.getVotes().entrySet().stream().collect(Collectors.toMap(key -> key.getKey().toString(), entry -> entry.getValue().toString())))
                .players(source.getPlayers().stream().map(item -> {
                    APlayer player = (APlayer) item;
                    return Player.builder().name(player.getName().toString()).email(player.getEmail().toString()).build();
                }).collect(Collectors.toList()))
                .locked(source.getLocked())
                .organizer(source.getOrganizer().toString())
                .resourceId(source.getResourceId().toString())
                .task(source.getTask().toString())
                .title(source.getTitle().toString())
                .build();
    }

    public static AScrum from(Scrum source) {
        return AScrum.newBuilder()
                .setChoices(source.getChoices().stream().collect(Collectors.toList()))
                .setVotes(source.getVotes().entrySet().stream().collect(Collectors.toMap(key -> key.getKey(), value -> value.getKey())))
                .setPlayers(source.getPlayers().stream().map(player -> APlayer.newBuilder()
                        .setEmail(player.getEmail())
                        .setName(player.getName()).build()).collect(Collectors.toList()))
                .setLocked(source.getLocked())
                .setOrganizer(source.getOrganizer())
                .setResourceId(source.getResourceId())
                .setTask(source.getTask())
                .setTitle(source.getTitle())
                .build();
    }
}
