package works.hop.reducer;

import works.hop.reducer.persist.JdbcState;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.persist.RecordKey;
import works.hop.reducer.state.AbstractReducer;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.State;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TodoListReducer<S> extends AbstractReducer<S> {

    public static final String INIT_LIST = "INIT_LIST";
    public static final String DROP_LIST = "DROP_LIST";
    public static final String FETCH_ALL = "FETCH_ALL";
    public static final String FETCH_BY_ID = "FETCH_BY_ID";
    public static final String CREATE_RECORD = "CREATE_RECORD";
    public static final String UPDATE_RECORD = "UPDATE_RECORD";
    public static final String DELETE_RECORD = "DELETE_RECORD";

    public TodoListReducer(String name, State<S> state) {
        super(name, state);
    }

    @Override
    public S reduce(State<S> state, Action action) {
        switch (action.getType().get()) {
            case INIT_LIST:
                RecordEntity newList = (RecordEntity) action.getBody();
                String recordId = UUID.randomUUID().toString();
                newList.getKey().setRecordId(recordId);
                ((JdbcState) state).save(newList);
                return state.apply((String) newList.getKey().getRecordId());
            case CREATE_RECORD: {
                Todo toCreate = (Todo) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(toCreate.id);
                TodoListRecord record = TodoListRecord.builder().entity(entity).build();
                TodoList todoList = record.getValue();
                List<Todo> list = todoList.getTodos() != null ? todoList.getTodos() : new ArrayList<>();
                list.add(toCreate);
                todoList.setTodos(list);
                record.setValue(todoList);
                ((JdbcState) state).save(record.getEntity());
                return state.apply(toCreate.id);
            }
            case DELETE_RECORD: {
                Todo toDelete = (Todo) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(toDelete.id);
                TodoListRecord record = TodoListRecord.builder().entity(entity).build();
                TodoList todoList = record.getValue();
                List<Todo> list = todoList.getTodos() != null ? todoList.getTodos() : new ArrayList<>();
                list = list.stream().filter(item -> !item.task.equals(toDelete.task)).collect(Collectors.toList());
                todoList.setTodos(list);
                record.setValue(todoList);
                ((JdbcState) state).update(record.getEntity());
                return state.apply(toDelete.id);
            }
            case UPDATE_RECORD: {
                Todo toUpdate = (Todo) action.getBody();
                RecordEntity entity = ((JdbcState) state).fetch(toUpdate.id);
                TodoListRecord record = TodoListRecord.builder().entity(entity).build();
                TodoList todoList = record.getValue();
                List<Todo> list = todoList.getTodos() != null ? todoList.getTodos() : new ArrayList<>();
                list = list.stream().map(item -> {
                    if (item.task.equals(toUpdate.task)) {
                        return toUpdate;
                    }
                    return item;
                }).collect(Collectors.toList());
                todoList.setTodos(list);
                record.setValue(todoList);
                ((JdbcState) state).update(record.getEntity());
                return state.apply(toUpdate.id);
            }
            case DROP_LIST: {
                String toDelete = (String) action.getBody();
                ((JdbcState) state).delete(RecordKey.builder().recordId(toDelete).build());
                return state.apply(toDelete);
            }
            default:
                return null;
        }
    }

    @Override
    public Object compute(Action action) {
        switch (action.getType().get()) {
            case FETCH_ALL:
                RecordKey toFetch = (RecordKey) action.getBody();
                return this.state().apply((String) toFetch.getRecordId());
            case FETCH_BY_ID:
                String fetchId = (String) action.getBody();
                return ((JdbcState) this.state()).fetch(fetchId);
            default:
                return null;
        }
    }
}