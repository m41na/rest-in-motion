package works.hop.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import works.hop.core.JsonSupplier;
import works.hop.core.RestServer;
import works.hop.jetty.JettyServer;
import works.hop.jetty.UploadHandler;
import works.hop.jetty.websocket.JettyWsAdapter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static works.hop.core.RestServer.APP_CTX_KEY;
import static works.hop.jetty.JettyServer.createServer;

@RunWith(BasicJUnit4ClassRunner.class)
public class BasicJUnit4ClassRunnerTest {

    public static final Integer PORT = 8080;
    public static final String HOST = "127.0.0.1";
    public static final String BASE = "/";

    @BasicProvider
    public RestServer provider() throws Exception {
        Map<String, String> props = new HashMap<>();
        props.put(APP_CTX_KEY, BASE);
        JettyServer server = createServer(props);
        server.get("/", (request, response, promise) -> {
            response.ok(request.requestLine());
            promise.complete();
        });
        server.get("/two", (request, response, promise) -> {
            response.ok("get two");
            promise.complete();
        });
        server.get("/two/{id}", (request, response, promise) -> {
            Long id = request.longParam("id");
            response.ok("get two with param " + id);
            promise.complete();
        });
        server.get("/two/{id}/one/{name}", (request, response, promise) -> {
            Long id = request.longParam("id");
            String name = request.param("name");
            response.ok("get two with 2 params " + id + " and " + name);
            promise.complete();
        });
        server.post("/", "application/json", "application/json", (request, response, promise) -> {
            Content content = request.body(Content.class);
            assertEquals("Expecting Posted Hello", "Posted Hello", content.message);
            response.send("Post Ok");
            promise.complete();
        });
        server.post("/file", new UploadHandler(Path.of("target"), UploadHandler.defaultConfig()));
        server.put("/", "application/json", "application/json", emptyMap(), (request, response, promise) -> {
            Content content = request.body(Content.class);
            assertEquals("Expecting Put Hello", "Put Hello", content.message);
            response.send("Put Ok");
            promise.complete();
        });
        server.delete("/", (request, response, promise) -> {
            response.send("Delete Ok");
            promise.complete();
        });
        server.websocket("/events/*", () -> new JettyWsAdapter());
        server.listen(PORT, HOST);
        return server;
    }

    @Test
    public void testGET_ROOT_FromServer(HttpClient client) throws InterruptedException, IOException {
        String uri = String.format("http://%s:%d%s", HOST, PORT, BASE);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Contains 'GET / HTTP/1.1'", true, response.body().contains("GET / HTTP/1.1"));
    }

    @Test
    public void testGET_Using_Invalid_Path(HttpClient client) throws InterruptedException, IOException {
        String uri = String.format("http://%s:%d%s", HOST, PORT, "/invalid");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Contains 'Error 404 Not Found'", true, response.body().contains("Error 404 Not Found"));
    }

    @Test
    public void testGET_TWO_FromServer(HttpClient client) throws InterruptedException, IOException {
        String uri = String.format("http://%s:%d%s", HOST, PORT, "/two");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Contains 'get two'", true, response.body().contains("get two"));
    }

    @Test
    public void testGET_TWO_PARAM_1_FromServer(HttpClient client) throws InterruptedException, IOException {
        String uri = String.format("http://%s:%d%s", HOST, PORT, "/two/10");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Contains 'get two with param'", true, response.body().contains("get two with param"));
    }

    @Test
    public void testGET_TWO_PARAMS_2_FromServer(HttpClient client) throws InterruptedException, IOException {
        String uri = String.format("http://%s:%d%s", HOST, PORT, "/two/10/one/jim");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Contains 'get two with 2 params 10 and jim'", true, response.body().contains("get two with 2 params 10 and jim"));
    }

    @Test
    public void testPOST_Hello_To_Server(HttpClient client) throws InterruptedException, IOException {
        String uri = String.format("http://%s:%d%s", HOST, PORT, BASE);
        Content content = new Content("Posted Hello");
        String json = JsonSupplier.version2.get().writeValueAsString(content);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Contains 'Post Ok'", true, response.body().contains("Post Ok"));
    }

    @Test
    public void testPUT_Hello_To_Server(HttpClient client) throws InterruptedException, IOException {
        String uri = String.format("http://%s:%d%s", HOST, PORT, BASE);
        Content content = new Content("Put Hello");
        String json = JsonSupplier.version2.get().writeValueAsString(content);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Contains 'Put Ok'", true, response.body().contains("Put Ok"));
    }

    @Test
    public void testDELETE_Hello_From_Server(HttpClient client) throws InterruptedException, IOException {
        String uri = String.format("http://%s:%d%s", HOST, PORT, BASE);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(10))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Contains 'Delete Ok'", true, response.body().contains("Delete Ok"));
    }

    @Test
    public void testPOST_Upload_MULTI_PART_File_To_Server(HttpClient client) throws InterruptedException, IOException {
        String uri = String.format("http://%s:%d%s", HOST, PORT, "/file");
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            File uploadTarget = new File("pom.xml");
            HttpEntity data = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("fileContent", uploadTarget, ContentType.DEFAULT_BINARY, uploadTarget.getName())
                    .addTextBody("text", "sample upload file", ContentType.DEFAULT_BINARY)
                    .build();

            HttpUriRequest request = RequestBuilder
                    .post(uri)
                    .setEntity(data)
                    .build();

            System.out.println("Executing request " + request.getRequestLine());

            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String response = httpclient.execute(request, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(response);
            assertEquals("Contains 'Got Part'", true, response.contains("Got Part"));
        }
    }

    static class Content {

        public final String message;

        @JsonCreator
        public Content(@JsonProperty("message") String message) {
            this.message = message;
        }
    }
}