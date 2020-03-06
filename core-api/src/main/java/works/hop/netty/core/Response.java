package works.hop.netty.core;

public interface Response {

    <T> ResponseEntity<T> result();
}
