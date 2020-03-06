package works.hop.core;

public interface ARequest<REQ> {

    REQ request();

    <T> Headers<T> headers();

    <T> ARequestEntity<T> body();
}
