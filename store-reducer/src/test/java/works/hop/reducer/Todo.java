package works.hop.reducer;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class Todo {
    public final Long id;
    public final String name;
    public final Boolean completed;
}
