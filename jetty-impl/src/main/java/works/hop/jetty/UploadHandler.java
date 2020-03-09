package works.hop.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;
import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.handler.HandlerException;
import works.hop.handler.HandlerFunction;
import works.hop.handler.HandlerPromise;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class UploadHandler implements HandlerFunction {

    private final MultipartConfigElement multipartConfig;
    private final Path outputDir;

    public UploadHandler(Path outputDir, MultipartConfigElement multipartConfig) throws IOException {
        super();
        this.multipartConfig = multipartConfig;
        this.outputDir = outputDir.resolve("handler");
        ensureDirExists(this.outputDir);
    }

    private static Path ensureDirExists(Path path) throws IOException {
        Path dir = path.toAbsolutePath();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        return dir;
    }

    private static ByteBuffer processParts(HttpServletRequest request, HttpServletResponse response, Path outputDir) throws ServletException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        for (Part part : request.getParts()) {
            buffer.put(String.format("Got Part[%s].size=%s%n", part.getName(), part.getSize()).getBytes());
            buffer.put(String.format("Got Part[%s].contentType=%s%n", part.getName(), part.getContentType()).getBytes());
            buffer.put(String.format("Got Part[%s].submittedFileName=%s%n", part.getName(), part.getSubmittedFileName()).getBytes());
            String filename = part.getSubmittedFileName();
            if (StringUtil.isNotBlank(filename)) {
                // ensure we don't have "/" and ".." in the raw form.
                filename = URLEncoder.encode(filename, "utf-8");

                Path outputFile = outputDir.resolve(filename);
                try (InputStream inputStream = part.getInputStream();
                     OutputStream outputStream = Files.newOutputStream(outputFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    IO.copy(inputStream, outputStream);
                    buffer.put(String.format("Saved Part[%s] to %s%n", part.getName(), outputFile.toString()).getBytes());
                }
            }
        }
        return buffer;
    }

    public static MultipartConfigElement defaultConfig() throws IOException {
        // MultiPartConfig setup - to allow for ServletRequest.getParts() usage
        Path multipartTmpDir = Paths.get("target", "multipart-tmp");
        multipartTmpDir = ensureDirExists(multipartTmpDir);

        String location = multipartTmpDir.toString();
        long maxFileSize = 10 * 1024 * 1024; // 10 MB
        long maxRequestSize = 10 * 1024 * 1024; // 10 MB
        int fileSizeThreshold = 64 * 1024; // 64 KB
        MultipartConfigElement multipartConfig = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
        return multipartConfig;
    }

    @Override
    public void handle(ARequest request, AResponse response, HandlerPromise promise) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        try {
            if (!request.method().equalsIgnoreCase("POST")) {
                throw new HandlerException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Use POST method to upload content");
            }
            // Ensure request knows about MultiPartConfigElement setup.
            httpServletRequest.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, multipartConfig);
            // Process the request
            ByteBuffer buffer = processParts(httpServletRequest, httpServletResponse, outputDir);
            buffer.flip();
            response.status(200);
            response.bytes(buffer.array());
        } catch (IOException | ServletException e) {
            throw new HandlerException(500, "Encountered error uploading content", e);
        } finally {
            promise.complete();
        }
    }
}