package works.hop.reducer.persist;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.SerializationUtils;
import works.hop.reducer.state.AbstractReducer;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.State;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

public class JdbcReducer<S> extends AbstractReducer<Map<String, Map<String, S>>> implements Crud<RecordValue> {

    public static final String FETCH_ALL = "FETCH_ALL";
    public static final String CREATE_RECORD = "CREATE_RECORD";
    public static final String UPDATE_RECORD = "UPDATE_RECORD";
    public static final String DELETE_RECORD = "DELETE_RECORD";

    private final JdbcTemplate template;

    public JdbcReducer(DataSource ds, String name, Map<String, Map<String, S>> initialState) {
        super(name, initialState);
        this.template = new JdbcTemplate(ds);
    }

    @Override
    public List<RecordValue> fetch(RecordKey key) {
        String FETCH_ALL = "select * from tbl_red_store where user_key = ? and collection_key = ? order by user_key, collection_key, data_id"; //default order is asc
        List<RecordEntity> rows = template.query(FETCH_ALL, new Object[]{key.getUserKey(), key.getCollectionKey()}, (rs, rowNum) ->
                RecordEntity.builder()
                        .key(RecordKey.builder()
                                .id(rs.getLong("data_id"))
                                .userKey(rs.getString("user_key"))
                                .collectionKey(rs.getString("collection_key")).build())
                        .value((RecordValue) SerializationUtils.deserialize(rs.getBytes("data_value")))
                        .dateCreated(new Date(rs.getDate("date_created").getTime()))
                        .build());
        return rows.stream().map(row -> {
            RecordValue data = row.getValue();
            data.setId(row.getKey().getId());
            return data;
        }).collect(Collectors.toList());
    }

    @Override
    public long save(RecordKey key, RecordValue record) {
        String CREATE_RECORD = "insert into tbl_red_store (user_key, collection_key, data_value, date_created) values (?, ?, ?, current_timestamp)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_RECORD, new String[]{"data_id"}); //or Statement.RETURN_GENERATED_KEYS
            ps.setString(1, key.getUserKey());
            ps.setString(2, key.getCollectionKey());
            ps.setBytes(3, SerializationUtils.serialize(record));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public int update(RecordValue record) {
        String UPDATE_RECORD = "update tbl_red_store set data_value = ? where data_id = ?";
        return template.update(UPDATE_RECORD, record.getId(), SerializationUtils.serialize(record));
    }

    @Override
    public int delete(Long id) {
        String DELETE_RECORD = "delete from tbl_red_store where data_id = ?";
        return template.update(DELETE_RECORD, id);
    }

    @Override
    public State<Map<String, Map<String, S>>> reduce(State<Map<String, Map<String, S>>> state, Action action) {
        switch (action.getType().get()) {
            case "FETCH_ALL":
                RecordKey toFetch = (RecordKey) action.getBody();
                List<RecordValue> records = fetch(toFetch);
                Map<String, Map<String, S>> fetchedState = state.get();
                fetchedState.putIfAbsent(toFetch.getUserKey(), new HashMap<>());
                fetchedState.get(toFetch.getUserKey()).putIfAbsent(toFetch.getCollectionKey(), (S) records);
                return () -> fetchedState;
            case "CREATE_RECORD":
                RecordEntity toCreate = (RecordEntity) action.getBody();
                long added = save(RecordKey.builder().userKey(toCreate.getKey().getUserKey()).collectionKey(toCreate.getKey().getCollectionKey()).build(), toCreate.getValue());
                if (added > 0) { //if created successfully
                    //add empty collections if need be
                    if (state.get().get(toCreate.getKey().getUserKey()) == null) {
                        Map<String, S> collections = new HashMap<>();
                        collections.put(toCreate.getKey().getCollectionKey(), (S) new ArrayList<>());
                        state.get().put(toCreate.getKey().getUserKey(), collections);
                    }
                    S list = state.get().get(toCreate.getKey().getUserKey()).get(toCreate.getKey().getCollectionKey());
                    List<RecordValue> values = (List<RecordValue>) list;
                    RecordValue addedValue = toCreate.getValue();
                    addedValue.setId(added);
                    values.add(addedValue);
                    //replace value in map
                    Map<String, Map<String, S>> newState = state.get();
                    newState.get(toCreate.getKey().getUserKey()).put(toCreate.getKey().getCollectionKey(), (S) values);
                    return () -> newState;
                }
                return () -> state.get();
            case "REMOVE_TODO":
                RecordKey toDelete = (RecordKey) action.getBody();
                int deleted = delete(toDelete.getId());
                if (deleted == 1) { //if deleted successfully
                    S list = state.get().get(toDelete.getUserKey()).get(toDelete.getCollectionKey());
                    List<RecordValue> values = ((List<RecordValue>) list).stream().filter(item -> !item.getId().equals(toDelete.getId())).collect(Collectors.toList());
                    //replace value in map
                    Map<String, Map<String, S>> newState = state.get();
                    newState.get(toDelete.getUserKey()).put(toDelete.getCollectionKey(), (S) values);
                    return () -> newState;
                }
                return () -> state.get();
            case "UPDATE_RECORD":
                RecordEntity toUpdate = (RecordEntity) action.getBody();
                int updated = update(toUpdate.getValue());
                if (updated == 1) { //if updated successfully
                    S list = state.get().get(toUpdate.getKey().getUserKey()).get(toUpdate.getKey().getCollectionKey());
                    List<RecordValue> values = ((List<RecordValue>) list).stream().map(item -> {
                        if (item.getId().equals(toUpdate.getKey().getId())) {
                            return toUpdate.getValue();
                        }
                        return item;
                    }).collect(Collectors.toList());
                    //replace value in map
                    Map<String, Map<String, S>> newState = state.get();
                    newState.get(toUpdate.getKey().getUserKey()).put(toUpdate.getKey().getCollectionKey(), (S) values);
                    return () -> newState;
                }
                return () -> state.get();
            default:
                return () -> state.get();
        }
    }
}