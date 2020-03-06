package works.hop.core;

import java.io.PrintWriter;

public interface AResponse<RES> {

    RES response();

    <T> AResponseEntity<T> result();

    void setContentType(String s);

    void setStatus(int scOk);

    PrintWriter getWriter();
}
