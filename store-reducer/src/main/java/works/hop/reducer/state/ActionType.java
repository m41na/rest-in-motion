package works.hop.reducer.state;

import java.util.function.Supplier;

@FunctionalInterface
public interface ActionType extends Supplier<String> {
}
