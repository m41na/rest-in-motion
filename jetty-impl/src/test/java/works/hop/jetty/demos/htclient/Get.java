package works.hop.jetty.demos.htclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Get {

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.cnn.com"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .header("USer-Agent", "Java 11")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println(response.body());
            } else {
                System.out.println("Something we wrong with the request");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }
}
