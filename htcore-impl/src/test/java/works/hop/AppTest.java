package works.hop;

import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.*;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class AppTest {

    @Test
    public void testingBasicHttpRequest() {
        HttpRequest request = new BasicHttpRequest("GET", "/", HttpVersion.HTTP_1_1);
        System.out.println(request.getRequestLine().getMethod());
        System.out.println(request.getRequestLine().getUri());
        System.out.println(request.getProtocolVersion());
        System.out.println(request.getRequestLine().toString());
    }

    @Test
    public void testBasicHttpResponse() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        System.out.println(response.getProtocolVersion());
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(response.getStatusLine().getReasonPhrase());
        System.out.println(response.getStatusLine().toString());

    }

    @Test
    public void testingBasicHttpResponseWithHeaders() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        response.addHeader("Set-Cookie", "c1=a; path=/; domain=localhost");
        response.addHeader("Set-Cookie", "c2=b; path=\"/\", c3=c; domain=\"localhost\"");
        Header h1 = response.getFirstHeader("Set-Cookie");
        System.out.println(h1);
        Header h2 = response.getLastHeader("Set-Cookie");
        System.out.println(h2);
        Header[] hs = response.getHeaders("Set-Cookie");
        System.out.println(hs.length);
    }

    @Test
    public void testingBasicHttpResponseWithHeadersIterator() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        response.addHeader("Set-Cookie", "c1=a; path=/; domain=localhost");
        response.addHeader("Set-Cookie", "c2=b; path=\"/\", c3=c; domain=\"localhost\"");
        HeaderIterator it = response.headerIterator("Set-Cookie");
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    @Test
    public void testingBasicHttpResponseWithHeaderElementsIterator() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        response.addHeader("Set-Cookie", "c1=a; path=/; domain=localhost");
        response.addHeader("Set-Cookie", "c2=b; path=\"/\", c3=c; domain=\"localhost\"");
        HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Set-Cookie"));
        while (it.hasNext()) {
            HeaderElement elem = it.nextElement();
            System.out.println(elem.getName() + " = " + elem.getValue());
            NameValuePair[] params = elem.getParameters();
            for (int i = 0; i < params.length; i++) {
                System.out.println(" " + params[i]);
            }
        }
    }

    @Test
    public void testHttpStringEntity() throws IOException {
        StringEntity myEntity = new StringEntity("important message", Consts.UTF_8);
        System.out.println(myEntity.getContentType());
        System.out.println(myEntity.getContentLength());
        System.out.println(EntityUtils.toString(myEntity));
        System.out.println(EntityUtils.toByteArray(myEntity).length);

        StringBuilder sb = new StringBuilder();
        Map<String, String> env = System.getenv();
        for (Map.Entry<String, String> envEntry : env.entrySet()) {
            sb.append(envEntry.getKey())
                    .append(": ").append(envEntry.getValue())
                    .append("\r\n");
        }
// construct without a character encoding (defaults to ISO-8859-1)
        HttpEntity myEntity1 = new StringEntity(sb.toString());
// alternatively construct with an encoding (mime type defaults to "text/plain")
        HttpEntity myEntity2 = new StringEntity(sb.toString(), Consts.UTF_8);
// alternatively construct with an encoding and a mime type
        HttpEntity myEntity3 = new StringEntity(sb.toString(),
                ContentType.create("text/plain", Consts.UTF_8));
    }

    @Test(expected = ProtocolException.class)
    public void testProtocolProcessors() throws IOException, HttpException {
        //NOTE:  BasicHttpProcessor class does not synchronize access to its internal structures and
        //therefore may not be thread-safe
        HttpProcessor httpProc = HttpProcessorBuilder.create()
                // Required protocol interceptors
                .add(new RequestContent())
                .add(new RequestTargetHost())
                // Recommended protocol interceptors
                .add(new RequestConnControl())
                .add(new RequestUserAgent("MyAgent-HTTP/1.1"))
                // Optional protocol interceptors
                .add(new RequestExpectContinue(true))
                .build();
        HttpCoreContext context = HttpCoreContext.create();
        HttpRequest request = new BasicHttpRequest("GET", "/");
        httpProc.process(request, context);
    }
}
