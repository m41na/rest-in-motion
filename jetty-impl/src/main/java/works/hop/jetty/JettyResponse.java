package works.hop.jetty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.core.AResponse;
import works.hop.core.BodyWriter;
import works.hop.core.ObjectMapperSupplier;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_NOT_ACCEPTABLE;
import static javax.servlet.http.HttpServletResponse.SC_SEE_OTHER;

public class JettyResponse implements AResponse<HttpServletResponse> {

    public static final Logger LOG = LoggerFactory.getLogger(JettyResponse.class);

    private final HttpServletResponse response;
    protected byte[] content;
    protected boolean redirect = false;
    protected boolean forward = false;
    protected String routeUri;
    protected String contextPath;
    protected ObjectMapper jsonMapper;
    protected ObjectMapper xmlMapper;

    public JettyResponse(HttpServletResponse response) {
        this.response = response;
        this.initialize();
    }

    @Override
    public void initialize() {
        this.response.setContentType("text/html;charset=utf-8");
        this.jsonMapper = ObjectMapperSupplier.version1.get();
        this.xmlMapper = ObjectMapperSupplier.xmlVersion.get();
    }

    @Override
    public HttpServletResponse response() {
        return response;
    }

    @Override
    public void setStatus(int status) {
        this.response.setStatus(status);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return this.response.getWriter();
    }

    @Override
    public void header(String header, String value) {
        response.setHeader(header, value);
    }

    @Override
    public void context(String ctx) {
        this.contextPath = ctx;
    }

    @Override
    public void status(int status) {
        setStatus(status);
    }

    public void ok(Object payload) {
        status(200);
        json(payload);
    }

    public void accepted() {
        sendStatus(201);
    }

    @Override
    public void sendStatus(int status) {
        status(status);
        send(HttpStatus.getMessage(status));
    }

    @Override
    public void send(String payload) {
        this.content = payload.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void send(int status, String payload) {
        status(status);
        send(payload);
    }

    @Override
    public void bytes(byte[] payload) {
        this.content = payload;
    }

    @Override
    public void json(Object payload) {
        try {
            this.response.setContentType("application/json");
            this.content = jsonMapper.writeValueAsBytes(payload);
        } catch (Exception e) {
            throw new RuntimeException("Could not write json value from java entity", e);
        }
    }

    @Override
    public void jsonp(Object payload) {
        try {
            this.response.setContentType("application/json");
            this.content = jsonMapper.writeValueAsBytes(payload);
        } catch (Exception e) {
            throw new RuntimeException("Could not write json value from java entity", e);
        }
    }

    @Override
    public void xml(Object payload) {
        try {
            this.content = xmlMapper.writeValueAsBytes(payload);
        } catch (JsonProcessingException e) {
            LOG.error("Could not transform content dest response body");
        };
    }

    @Override
    public <T> void content(T payload, BodyWriter<T> writer) {
        byte[] bytes = writer.transform(payload);
        this.response.setContentLength(bytes.length);
        this.content = bytes;
    }

    @Override
    public void render(String template, Map<String, Object> model) {
        //TODO: not yet supported
    }

    @Override
    public void next(String path) {
        this.forward = true;
        this.routeUri = path;
    }

    @Override
    public void redirect(String path) {
        this.redirect = true;
        this.routeUri = path;
        setStatus(SC_SEE_OTHER);
    }

    @Override
    public void redirect(int status, String path) {
        this.redirect = true;
        this.routeUri = path;
        setStatus(status);
    }

    @Override
    public void type(String mimetype) {
        this.response.setContentType(mimetype);
    }

    @Override
    public void cookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        this.response.addCookie(cookie);
    }

    @Override
    public void attachment(String filename) {
        download(filename, filename, this.response.getContentType());
    }

    @Override
    public void download(String path, String filename, String mimeType) {
        // reads input file from an absolute path
        Path filePath = Paths.get(System.getProperty("user.dir"), path);
        File downloadFile = filePath.resolve(filename).toFile();

        // gets MIME type of the file
        if (mimeType == null) {
            // set dest binary type if MIME context not provided
            mimeType = "application/octet-stream";
        }

        // modifies response
        this.response.setContentType(mimeType);

        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        this.response.setHeader(headerKey, headerValue);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
        byte[] buffer = new byte[4096];
        int bytesRead;

        try (FileInputStream inStream = new FileInputStream(downloadFile)) {
            while ((bytesRead = inStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            setStatus(SC_NOT_ACCEPTABLE);
            send(ex.getMessage());
            return;
        }
        this.content = baos.toByteArray();
    }

    @Override
    public byte[] getContent() {
        return this.content;
    }

    @Override
    public String readContent(String folder, String file) {
        Path path = Paths.get(folder, file);
        try {
            StringWriter sw = new StringWriter();
            Files.lines(path).forEach(sw::write);
            return sw.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Reader getReader(String folder, String file) {
        Path path = Paths.get(folder, file);
        try {
            return new FileReader(path.toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}