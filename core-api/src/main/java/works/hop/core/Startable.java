package works.hop.core;

import java.util.function.Consumer;

public interface Startable {

    <R extends Restful> R rest();

    String status();

    // ************* console splash banner ****************** //
    void banner();

    // ************* START *****************//
    void start() throws Exception;

    // ************* READY *****************//
    void listen(Integer port, String host);

    void listen(Integer port, String host, Consumer<String> result);

    // ************* STOP *****************//
    void shutdown() throws Exception;
}
