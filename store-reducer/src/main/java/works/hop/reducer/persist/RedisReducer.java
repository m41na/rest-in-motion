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
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RedisReducer<S> extends AbstractReducer<S> implements Crud<RecordValue> {

    public static final String FETCH_ALL = "FETCH_ALL";
    public static final String CREATE_RECORD = "CREATE_RECORD";
    public static final String UPDATE_RECORD = "UPDATE_RECORD";
    public static final String DELETE_RECORD = "DELETE_RECORD";

    private static final Logger LOG = LoggerFactory.getLogger(RedisReducer.class);
    private final JdbcTemplate template;

    public RedisReducer(DataSource ds, String name, S initialState) {
        super(name, initialState);
        this.template = new JdbcTemplate(ds);
    }

    @Override
    public List<RecordValue> fetchAll(String userKey, String collectionKey) {
        String FETCH_ALL = "select * from tbl_red_store";
        List<RecordEntity> rows = template.query(FETCH_ALL, (rs, rowNum) ->
                RecordEntity.builder().id(rs.getLong("data_id")).userKey(rs.getString("user_key"))
                        .collectionKey(rs.getString("collection_key"))
                        .dataValue((RecordValue) SerializationUtils.deserialize(rs.getBytes("data_value")))
                        .dateCreated(new Date(rs.getDate("date_created").getTime()))
                        .build());
        return rows.stream().map(row -> {
            RecordValue data = row.getDataValue();
            data.setId(row.getId());
            return data;
        }).collect(Collectors.toList());
    }

    @Override
    public long save(String userKey, String collectionKey, RecordValue record) {
        String CREATE_RECORD = "insert into tbl_red_store (user_key, collection_key, data_value, date_created) values (?, ?, ?, current_timestamp)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_RECORD, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userKey);
            ps.setString(2, collectionKey);
            ps.setBytes(3, SerializationUtils.serialize(record));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public int update(String userKey, String collectionKey, RecordValue record) {
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
                List<RecordValue> records = fetchAll(toFetch.getUserKey(), toFetch.getCollectionKey());
                return () -> (S) records;
            case "CREATE_RECORD":
                RecordEntity toCreate = (RecordEntity) action.getBody();
                long added = save(toCreate.getUserKey(), toCreate.getCollectionKey(), toCreate.getDataValue());
                assert added > 0;
                return () -> (S) fetchAll(toCreate.getUserKey(), toCreate.getCollectionKey());
            case "REMOVE_TODO":
                RecordCriteria toDelete = (RecordCriteria) action.getBody();
                int deleted = delete(toDelete.getUserKey(), toDelete.getCollectionKey(), toDelete.getId());
                assert deleted == 1;
                return () -> (S) fetchAll(toDelete.getUserKey(), toDelete.getCollectionKey());
            case "UPDATE_RECORD":
                RecordEntity toUpdate = (RecordEntity) action.getBody();
                int updated = update(toUpdate.getUserKey(), toUpdate.getCollectionKey(), toUpdate.getDataValue());
                assert updated == 1;
                return () -> (S) fetchAll(toUpdate.getUserKey(), toUpdate.getCollectionKey());
            default:
                return state;
        }
    }
}