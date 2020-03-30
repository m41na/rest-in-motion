package works.hop.reducer;

import org.cache2k.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("todo-cache")
public class TodoCache implements TodoService{

    private final TodoRepository repository;
    private final Cache<String, Todo> cachedDiscounts;

    public TodoCache(@Autowired TodoRepository repository, @Autowired Cache<String, Todo> cachedDiscounts) {
        this.repository = repository;
        this.cachedDiscounts = cachedDiscounts;
    }

    @Override
    public List<Todo> fetchAll() {
        return null;
    }

    @Override
    public Todo save(String task) {
        return null;
    }

    @Override
    public int update(String task) {
        return 0;
    }

    @Override
    public int remove(String task) {
        return 0;
    }
}
