package works.hop.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AResponse<RES> {

    void initialize();

    RES response();

    void setStatus(int scOk);

    PrintWriter getWriter() throws IOException;

    void header(String header, String value);

    void context(String ctx);

    void status(int status);

    void sendStatus(int status);

    void ok(Object payload);

    void accepted();

    void send(String payload);

    void send(int status, String payload);

    void bytes(byte[] payload);

    void json(Object payload);

    void jsonp(Object payload);

    void xml(Object payload);

    <T> void content(T payload, BodyWriter<T> writer);

    void render(String template, Map<String, Object> model);

    void next(String path);

    void redirect(String path);

    void redirect(int status, String path);

    void type(String mimetype);

    void cookie(String name, String value);

    void attachment(String filename);

    void download(String path, String filename, String mimeType);

    byte[] getContent();

    String readContent(String folder, String file);

    Reader getReader(String folder, String file);
}
