package works.hop.reducer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Todo implements Serializable {

    public final Long id;
    public final String task;
    public final Boolean completed;

    @JsonCreator
    public Todo(@JsonProperty("id") Long id, @JsonProperty("task") String task, @JsonProperty("completed") Boolean completed) {
        this.id = id;
        this.task = task;
        this.completed = completed;
    }
}
