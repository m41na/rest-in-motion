package works.hop.reducer;

import java.util.List;

public interface TodoService {

    List<Todo> fetchAll();

    Todo save(String task);

    int update(String task);

    int remove(String task);
}
