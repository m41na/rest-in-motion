package works.hop.reducer.persist;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.SerializationUtils;
import works.hop.reducer.state.State;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JdbcState<S> implements Crud, State<S> {

    private final JdbcTemplate template;

    public JdbcState(DataSource ds) {
        this.template = new JdbcTemplate(ds);
    }

    @Override
    public List<RecordValue> fetch(RecordKey key) {
        String FETCH_QUERY = "select * from tbl_red_store where user_key = ? and collection_key = ? order by user_key, collection_key, data_id"; //default order is asc
        List<RecordEntity> rows = template.query(FETCH_QUERY, new Object[]{key.getUserKey(), key.getCollectionKey()}, (rs, rowNum) ->
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
    public RecordValue fetch(long id) {
        String FETCH_BY_ID_QUERY = "select data_value from tbl_red_store where data_id = ?";
        return template.queryForObject(FETCH_BY_ID_QUERY, new Object[]{id}, (rs, rowNum) ->
                (RecordValue) SerializationUtils.deserialize(rs.getBytes("data_value")));
    }

    @Override
    public long save(RecordEntity record) {
        String CREATE_QUERY = "insert into tbl_red_store (user_key, collection_key, data_value, date_created) values (?, ?, ?, current_timestamp)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, new String[]{"data_id"}); //or Statement.RETURN_GENERATED_KEYS
            ps.setString(1, record.getKey().getUserKey());
            ps.setString(2, record.getKey().getCollectionKey());
            ps.setBytes(3, SerializationUtils.serialize(record.getValue()));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public int update(RecordEntity record) {
        String UPDATE_QUERY = "update tbl_red_store set data_value = ? where data_id = ?";
        return template.update(UPDATE_QUERY, SerializationUtils.serialize(record.getValue()), record.getKey().getId());
    }

    @Override
    public int delete(RecordKey key) {
        String DELETE_QUERY = "delete from tbl_red_store where data_id = ?";
        return template.update(DELETE_QUERY, key.getId());
    }

    @Override
    public S apply(String user, String collection) {
        return (S) fetch(RecordKey.builder().userKey(user).collectionKey(collection).build());
    }

    @Override
    public void accept(String user, String collection, S state) {
        System.out.println("do nothing. simply acknowledge new state was accepted -> " + state);
    }
}
