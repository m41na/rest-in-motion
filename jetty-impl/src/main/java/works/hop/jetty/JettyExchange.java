package works.hop.jetty;

import works.hop.core.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class JettyExchange implements Exchange {

    private final ARequest ARequest;
    private final AResponse AResponse;

    public JettyExchange(ARequest ARequest, AResponse AResponse) {
        this.ARequest = ARequest;
        this.AResponse = AResponse;
    }

    @Override
    public ARequest request() {
        return ARequest;
    }

    @Override
    public AResponse response() {
        return AResponse;
    }

    public static class JettyARequest implements ARequest<HttpServletRequest> {

        private final HttpServletRequest request;

        public JettyARequest(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        public HttpServletRequest request() {
            return request;
        }

        @Override
        public <T> Headers<T> headers() {
            return null;
        }

        @Override
        public <T> ARequestEntity<T> body() {
            return null;
        }
    }

    public static class JettyAResponse implements AResponse<HttpServletResponse> {

        private final HttpServletResponse response;

        public JettyAResponse(HttpServletResponse response) {
            this.response = response;
        }

        @Override
        public HttpServletResponse response() {
            return response;
        }

        @Override
        public <T> AResponseEntity<T> result() {
            return null;
        }

        @Override
        public void setContentType(String s) {

        }

        @Override
        public void setStatus(int scOk) {

        }

        @Override
        public PrintWriter getWriter() {
            return null;
        }
    }
}
