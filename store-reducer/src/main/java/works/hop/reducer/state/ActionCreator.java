package works.hop.reducer.state;

import java.util.function.Function;

public class ActionCreator {

    public <V, T> Function<V, Action<T>> create(ActionType type) {
        return data -> new Action() {
            @Override
            public ActionType getType() {
                return type;
            }

            @Override
            public V getBody() {
                return data;
            }
        };
    }
}
