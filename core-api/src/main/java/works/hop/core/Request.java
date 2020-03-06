package works.hop.core;

public interface Request {

    <T> Headers<T> headers();

    <T> RequestEntity<T> body();
}
