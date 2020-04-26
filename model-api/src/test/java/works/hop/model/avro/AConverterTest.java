package works.hop.model.avro;

import junit.framework.TestCase;
import works.hop.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AConverterTest extends TestCase {

    public void testFromTodoToATodo() {
        ATodo todo = new ATodo(1l, "buy milk", false);
        Todo result = AConverter.from(todo);
        assertEquals(todo.getId(), result.getId().longValue());
        assertEquals(todo.getTask(), result.getTask());
        assertEquals(todo.getCompleted(), result.getCompleted().booleanValue());
    }

    public void testFromTodoListToTaskList() {
        TaskList tasks = new TaskList();
        List<Object> lists = new ArrayList<>();
        List<Task> first = new ArrayList<>();
        first.add(new Task(0l, "buy grass", true));
        lists.add(first);
        List<Task> second = new ArrayList<>();
        second.add(new Task(0l, "feed cow", false));
        lists.add(second);
        tasks.setList(lists);
        assertEquals(2, tasks.getList().size());
        TodoList list = AConverter.from(tasks);
        assertEquals(2, list.getList().size());
    }

    public void testFromTaskListToTodoList() {
        List<Todo> first = new ArrayList<>();
        first.add(Todo.builder().task("buy grass").completed(true).build());
        List<Todo> second = new ArrayList<>();
        second.add(Todo.builder().task("feed cow").completed(false).build());
        List<List<Todo>> lists = Arrays.asList(first, second);
        TodoList list = TodoList.builder().list(lists).build();
        assertEquals(2, list.getList().size());
    }

    public void testFromAProfileToProfile() {
        AProfile source = AProfile.newBuilder()
                .setFirstName("Steve")
                .setLastName("mikes")
                .setAge(25)
                .setHeight(6.3f)
                .setRegistered(true)
                .setPhoneNumbers(Arrays.asList(
                        APhone.newBuilder()
                                .setType("Home")
                                .setNumber("4343434345").build())
                ).setFloats(Arrays.asList(1.2f, 3.4f, 5.6f))
                .setIntegers(Arrays.asList(1, 2, 3, 4))
                .setStrings(Arrays.asList("ones", "twos"))
                .setBooleans(Arrays.asList(false, true, false)).build();
        Profile profile = AConverter.from(source);
        assertEquals(source.getFirstName(), profile.getFirstName());
        assertEquals(source.getIntegers().size(), profile.getIntegers().size());
    }

    public void testFromAScrumToScrum() {
        Scrum scrum = Scrum.builder()
                .choices(Arrays.asList("1", "3", "7", "12"))
                .title("a little house on a rock")
                .locked(false)
                .organizer("smikes@email.com")
                .resourceId("rPO0vf")
                .task("Task ABC-123")
                .players(Arrays.asList(
                        Player.builder().email("mikes@emal.com").name("mikes").build(),
                        Player.builder().email("janet@email.com").name("janet").build()))
                .votes(Arrays.asList(new String[]{"mikes", "1"}, new String[]{"janet", "5"})
                        .stream().collect(Collectors.toMap(key -> key[0], value -> value[1])))
                .build();
        //convert to avro object
        AScrum aScrum = AConverter.from(scrum);
        assertEquals(scrum.getChoices().size(), aScrum.getChoices().size());
        assertEquals(scrum.getPlayers().size(), aScrum.getPlayers().size());
        assertEquals(scrum.getVotes().size(), aScrum.getVotes().size());
        assertEquals(scrum.getTask(), aScrum.getTask());
        assertEquals(scrum.getTitle(), aScrum.getTitle());

        //convert from avro object
        Scrum result = AConverter.from(aScrum);
        assertEquals(scrum.getChoices().size(), result.getChoices().size());
        assertEquals(scrum.getPlayers().size(), result.getPlayers().size());
        assertEquals(scrum.getVotes().size(), result.getVotes().size());
        assertEquals(scrum.getTask(), result.getTask());
        assertEquals(scrum.getTitle(), result.getTitle());
    }
}