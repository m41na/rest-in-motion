package works.hop.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AResponseEntity<T> {

    public final T data;
    public final Map<String, String> errors;

    public AResponseEntity(T data, Map<String, String> errors) {
        this.data = data;
        this.errors = errors;
    }

    public static <T> AResponseEntity ok(T data) {
        return new AResponseEntity(data, Collections.emptyMap());
    }

    public static <T> AResponseEntity error(Throwable th) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", th.getMessage());
        return new AResponseEntity(null, errors);
    }
}
