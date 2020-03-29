package works.hop.reducer.state;

public interface Action<T> {

    ActionType getType();

    T getBody();
}
