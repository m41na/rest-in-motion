package works.hop.nosql.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AResult<T> {

    private final T body;
    private final String error;

    public AResult(T body, String error) {
        this.body = body;
        this.error = error;
    }

    public AResult(T body, Map<String, List<String>> errors) {
        this(body, errors != null && !errors.isEmpty() ?
                errors.keySet().stream().reduce("", (acc, key) -> (acc.length() > 0 ? acc.concat("\r\n") : acc).concat(
                        errors.get(key).stream().reduce("", (left, right) -> left.concat(";").concat(right)))) : "");
    }

    public static <T> AResult of(T body) {
        return new AResult(body, Collections.emptyMap());
    }

    public static <T> AResult empty(String error) {
        return new AResult(null, error);
    }

    public static <T> AResult empty(Map<String, List<String>> errors) {
        return new AResult(null, errors);
    }

    public T get() {
        return this.body;
    }

    public String error() {
        return this.error;
    }

    public Boolean isOk() {
        return body != null;
    }
}
