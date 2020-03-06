package works.hop.netty.core;

public interface Request {

    <T> Headers<T> headers();

    <T> RequestEntity<T> body();
}
