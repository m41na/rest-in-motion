package works.hop.core;

import java.util.Date;

public interface AuthInfo<T> {

    T user();

    String token();

    Date expiry();

    String[] roles();

    Boolean ghosting();

    AuthInfo<T> ghost();
}
