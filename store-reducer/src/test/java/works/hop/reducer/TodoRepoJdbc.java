package works.hop.reducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;

@Repository("todo-jdbc")
public class TodoRepoJdbc implements TodoService {

    private final JdbcTemplate template;

    public TodoRepoJdbc(@Autowired DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Todo> fetchAll() {
        String FETCH_ALL_TODO = "select * from tbl_todos";
        return template.query(FETCH_ALL_TODO, (rs, rowNum) ->
                Todo.builder().id(rs.getLong("id"))
                        .task(rs.getString("task"))
                        .completed(rs.getBoolean("completed"))
                        .build());
    }

    @Override
    public Todo save(String task) {
        String CREATE_TODO = "insert into tbl_todos (task) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_TODO);
            ps.setString(1, task);
            return ps;
        }, keyHolder);
        return Todo.builder().id(keyHolder.getKey().longValue()).task(task).completed(false).build();
    }

    @Override
    public int update(String task) {
        String TOGGLE_COMPLETE = "update tbl_todos set completed = !(select completed from tbl_todos where task = ?) where task = ?";
        return template.update(TOGGLE_COMPLETE, ps -> ps.setString(1, task));
    }

    @Override
    public int remove(String task) {
        String DELETE_TODO = "delete from tbl_todos where task = ?";
        return template.update(DELETE_TODO, ps -> ps.setString(1, task));
    }
}
