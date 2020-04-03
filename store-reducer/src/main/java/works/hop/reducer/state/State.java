package works.hop.reducer.state;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@FunctionalInterface
public interface State<S> extends Supplier<S> {

    default <T> Map<String, T> getMap() {
        return (Map) get();
    }

    default <T, R> Map<String, T> getMap(Map<String, R> input, String key) {
        return (Map) input.get(key);
    }

    default <T> List<T> getList() {
        return (List) get();
    }

    default <T, R> List<R> getList(Map<String, T> input, String key) {
        return (List) input.get(key);
    }
}
