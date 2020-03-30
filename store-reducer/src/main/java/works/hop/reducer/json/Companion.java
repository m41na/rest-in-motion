package works.hop.reducer.json;

import java.util.TreeSet;

public class Companion<T> extends TreeSet<Prop> {

    private final String name;

    public Companion(Class<T> type, T target) {
        this.name = type.getName().replaceAll("\\.", "/");
    }

    private void extractValues(T target) {

    }

    public String getName() {
        return this.name;
    }
}
