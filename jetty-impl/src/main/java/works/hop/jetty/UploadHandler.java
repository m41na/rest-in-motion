package works.hop.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class UploadHandler extends AbstractHandler {

    private final String contextPath;
    private final MultipartConfigElement multipartConfig;
    private final Path outputDir;

    public UploadHandler(String contextPath, Path outputDir, MultipartConfigElement multipartConfig) throws IOException {
        super();
        this.contextPath = contextPath;
        this.multipartConfig = multipartConfig;
        this.outputDir = outputDir.resolve("handler");
        ensureDirExists(this.outputDir);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!target.equals(contextPath)) {
            // not meant for us, skip it.
            return;
        }

        if (!request.getMethod().equalsIgnoreCase("POST")) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        // Ensure request knows about MultiPartConfigElement setup.
        request.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, multipartConfig);
        // Process the request
        processParts(request, response, outputDir);
        baseRequest.setHandled(true);
    }

    private static Path ensureDirExists(Path path) throws IOException {
        Path dir = path.toAbsolutePath();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        return dir;
    }

    private static void processParts(HttpServletRequest request, HttpServletResponse response, Path outputDir) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");

        PrintWriter out = response.getWriter();

        for (Part part : request.getParts()) {
            out.printf("Got Part[%s].size=%s%n", part.getName(), part.getSize());
            out.printf("Got Part[%s].contentType=%s%n", part.getName(), part.getContentType());
            out.printf("Got Part[%s].submittedFileName=%s%n", part.getName(), part.getSubmittedFileName());
            String filename = part.getSubmittedFileName();
            if (StringUtil.isNotBlank(filename)) {
                // ensure we don't have "/" and ".." in the raw form.
                filename = URLEncoder.encode(filename, "utf-8");

                Path outputFile = outputDir.resolve(filename);
                try (InputStream inputStream = part.getInputStream();
                     OutputStream outputStream = Files.newOutputStream(outputFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    IO.copy(inputStream, outputStream);
                    out.printf("Saved Part[%s] to %s%n", part.getName(), outputFile.toString());
                }
            }
        }
    }

    public static MultipartConfigElement defaultConfig() throws IOException {
        // Establish output directory
        Path outputDir = Paths.get("target", "upload-dir");
        outputDir = ensureDirExists(outputDir);

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
}