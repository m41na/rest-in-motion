package works.hop.reducer.state;

import java.util.function.BiFunction;

public interface State<S> extends BiFunction<String, String, S> {

    void accept(String user, String collection, S state);
}
