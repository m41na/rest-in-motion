package works.hop;

public interface Request {

    <T> Headers<T> headers();

    <T> RequestEntity<T> body();
}
