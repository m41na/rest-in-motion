package works.hop.netty.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResponseEntity<T> {

    public final T data;
    public final Map<String, String> errors;

    public ResponseEntity(T data, Map<String, String> errors) {
        this.data = data;
        this.errors = errors;
    }

    public static <T> ResponseEntity ok(T data) {
        return new ResponseEntity(data, Collections.emptyMap());
    }

    public static <T> ResponseEntity error(Throwable th) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", th.getMessage());
        return new ResponseEntity(null, errors);
    }
}
