package works.hop.core;

public interface Response {

    <T> ResponseEntity<T> result();
}
