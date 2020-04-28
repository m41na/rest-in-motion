package works.hop.reducer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.persist.RecordValue;

import java.io.IOException;

@Data
@AllArgsConstructor
@Builder
public class TodoListRecord implements RecordValue<TodoList> {

    private final ObjectMapper mapper = new ObjectMapper();
    private RecordEntity entity;

    @Override
    public TodoList getValue() {
        try {
            return mapper.readValue(entity.getValue(), TodoList.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(TodoList todoList) {
        try {
            byte[] bytes = mapper.writeValueAsBytes(todoList);
            entity.setValue(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
