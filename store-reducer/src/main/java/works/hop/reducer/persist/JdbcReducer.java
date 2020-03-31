package works.hop.reducer.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.SerializationUtils;
import works.hop.reducer.state.AbstractReducer;
import works.hop.reducer.state.Action;
import works.hop.reducer.state.State;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JdbcReducer<S, T> extends AbstractReducer<S> implements Crud<T> {

    public static final String FETCH_ALL = "FETCH_ALL";
    public static final String CREATE_RECORD = "CREATE_RECORD";
    public static final String UPDATE_RECORD = "UPDATE_RECORD";
    public static final String DELETE_RECORD = "DELETE_RECORD";

    private static final Logger LOG = LoggerFactory.getLogger(JdbcReducer.class);
    private final JdbcTemplate template;

    public JdbcReducer(DataSource ds, S initialState) {
        super(initialState);
        this.template = new JdbcTemplate(ds);
    }

    @Override
    public List<T> fetchAll(String userKey, String collectionKey) {
        String FETCH_ALL = "select * from tbl_red_store";
        List<RecordValue> rows = template.query(FETCH_ALL, (rs, rowNum) ->
                RecordValue.builder().id(rs.getLong("data_id")).userKey(rs.getString("user_key"))
                        .collectionKey(rs.getString("collection_key"))
                        .dataValue(SerializationUtils.deserialize(rs.getBytes("data_value")))
                        .dateCreated(new Date(rs.getDate("date_created").getTime()))
                        .build());
        return rows.stream().map(row -> (T) row.getDataValue()).collect(Collectors.toList());
    }

    @Override
    public int save(String userKey, String collectionKey, T record) {
        String CREATE_RECORD = "insert into tbl_red_store (user_key, collection_key, data_value, date_created) values (?, ?, ?, current_timestamp)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_RECORD);
            ps.setString(1, userKey);
            ps.setString(2, collectionKey);
            ps.setBytes(3, SerializationUtils.serialize(record));
            return ps;
        }, keyHolder);
        return result;
    }

    @Override
    public int update(String userKey, String collectionKey, T record) {
        String UPDATE_RECORD = "update tbl_red_store set data_type = ? where user_key = ? and collection_key = ? and data_value = ?";
        return template.update(UPDATE_RECORD, userKey, collectionKey, SerializationUtils.serialize(record));
    }

    @Override
    public int delete(String userKey, String collectionKey, Long id) {
        String DELETE_RECORD = "delete from tbl_red_store where user_key = ? and collection_key = ? and id = ?";
        return template.update(DELETE_RECORD, userKey, collectionKey, id);
    }

    @Override
    public State<S> reduce(State<S> state, Action action) {
        switch (action.getType().get()) {
            case "FETCH_ALL":
                RecordCriteria toFetch = (RecordCriteria) action.getBody();
                List<T> records = fetchAll(toFetch.getUserKey(), toFetch.getCollectionKey());
                return () -> (S) records;
            case "CREATE_RECORD":
                RecordValue toCreate = (RecordValue) action.getBody();
                int added = save(toCreate.getUserKey(), toCreate.getCollectionKey(), (T) toCreate.getDataValue());
                assert added == 1;
                return () -> (S) fetchAll(toCreate.getUserKey(), toCreate.getCollectionKey());
            case "REMOVE_TODO":
                RecordCriteria toDelete = (RecordCriteria) action.getBody();
                int deleted = delete(toDelete.getUserKey(), toDelete.getCollectionKey(), toDelete.getId());
                assert deleted == 1;
                return () -> (S) fetchAll(toDelete.getUserKey(), toDelete.getCollectionKey());
            case "UPDATE_RECORD":
                RecordValue toUpdate = (RecordValue) action.getBody();
                int updated = update(toUpdate.getUserKey(), toUpdate.getCollectionKey(), (T) toUpdate.getDataValue());
                assert updated == 1;
                return () -> (S) fetchAll(toUpdate.getUserKey(), toUpdate.getCollectionKey());
            default:
                return state;
        }
    }
}