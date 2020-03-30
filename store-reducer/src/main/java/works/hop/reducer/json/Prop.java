package works.hop.reducer.json;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class Prop<T> {
    final String key;
    final T value;
}
