package works.hop;

public interface Response {

    <T> ResponseEntity<T> result();
}
