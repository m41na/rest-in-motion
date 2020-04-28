package works.hop.reducer.persist;

import org.springframework.jdbc.core.JdbcTemplate;
import works.hop.reducer.state.State;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

public class JdbcState<S> implements Crud, State<S> {

    private final JdbcTemplate template;

    public JdbcState(DataSource ds) {
        this.template = new JdbcTemplate(ds);
    }

    @Override
    public List<RecordEntity> fetch(RecordKey key) {
        String FETCH_QUERY = "select * from tbl_red_store where user_key = ? and collection_key = ? order by user_key, collection_key, data_id"; //default order is asc
        List<RecordEntity> rows = template.query(FETCH_QUERY, new Object[]{key.getUserKey(), key.getCollectionKey()}, (rs, rowNum) ->
                RecordEntity.builder()
                        .key(RecordKey.builder()
                                .recordId(rs.getString("data_id"))
                                .userKey(rs.getString("user_key"))
                                .collectionKey(rs.getString("collection_key")).build())
                        .value(rs.getBytes("data_value"))
                        .dateCreated(new Date(rs.getDate("date_created").getTime()))
                        .build());
        return rows;
    }

    @Override
    public RecordEntity fetch(String recordId) {
        String FETCH_BY_ID_QUERY = "select data_id, user_key, collection_key, data_value, date_created from tbl_red_store where data_id = ?";
        return template.queryForObject(FETCH_BY_ID_QUERY, new Object[]{recordId}, (rs, rowNum) ->
                RecordEntity.builder().key(RecordKey.builder()
                        .recordId(rs.getString("data_id"))
                        .userKey(rs.getString("user_key"))
                        .collectionKey(rs.getString("collection_key")).build())
                        .value(rs.getBytes("data_value"))
                        .dateCreated(new Date(rs.getDate("date_created").getTime())).build());
    }

    @Override
    public String save(RecordEntity record) {
        String CREATE_QUERY = "merge into tbl_red_store (data_id, user_key, collection_key, data_value, date_created) key (data_id) values (?, ?, ?, ?, current_timestamp)";
        int count = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_QUERY);
            ps.setString(1, (String) record.getKey().getRecordId());
            ps.setString(2, record.getKey().getUserKey());
            ps.setString(3, record.getKey().getCollectionKey());
            ps.setBytes(4, record.getValue());
            return ps;
        });
        return count > 0 ? (String) record.getKey().getRecordId() : null;
    }

    @Override
    public int update(RecordEntity record) {
        String UPDATE_QUERY = "update tbl_red_store set data_value = ? where data_id = ?";
        return template.update(UPDATE_QUERY, record.getValue(), record.getKey().getRecordId());
    }

    @Override
    public int delete(RecordKey key) {
        String DELETE_QUERY = "delete from tbl_red_store where data_id = ?";
        return template.update(DELETE_QUERY, key.getRecordId());
    }

    @Override
    public S apply(String recordId) {
        return (S) fetch(recordId);
    }

    @Override
    public void accept(String recordId, S state) {
        System.out.println("do nothing. simply acknowledge new state was accepted -> " + state);
    }
}
