package works.hop.route;

import com.fasterxml.jackson.annotation.JsonIgnore;
import works.hop.handler.Handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface Routing {

    interface Searchable<S, E> {

        void search(S criteria);

        boolean contains(S criteria);

        void info(List<String> nodes, String prefix);

        void add(E entity);

        void remove(E entity);
    }

    interface Router extends Searchable<Search, Route> {
    }

    class Search {

        //search attributes
        public final Attributes attributes;
        //result attributes
        public final Map<String, String> pathParams = new HashMap<>();
        @JsonIgnore
        public Route result;

        public Search(Attributes attributes) {
            super();
            this.attributes = attributes;
        }
    }

    class Attributes {

        public String url;
        public String method;
        public Map<String, String> headers = new HashMap<>();

        public String getHeader(String name) {
            return headers.containsKey(name) ? headers.get(name) : null;
        }

        @Override
        public String toString() {
            return "RequestAttrs [path=" + url + ", method=" + method + ", headers=" + headers + "]";
        }
    }

    class Route {

        public final String path;
        public final String accept;
        public final String contentType;
        public final Map<String, String> headers = new HashMap<>();
        public final Handler handler;
        public String method;

        public Route(Handler handler, String path, String method, String accept, String contentType, Map<String, String> headers) {
            this.path = path;
            this.method = method;
            this.accept = accept;
            this.contentType = contentType;
            this.headers.putAll(headers);
            this.handler = handler;
        }

        public Route(Handler handler, String... args) {
            this(handler,
                    args.length > 0 ? args[0] : null,
                    args.length > 1 ? args[1] : null,
                    args.length > 2 ? args[2] : null,
                    args.length > 3 ? args[3] : null,
                    Collections.emptyMap());
        }

        public Route(Handler handler, String path, String method, String accept, String contentType) {
            this(handler,
                    path,
                    method,
                    accept,
                    contentType,
                    Collections.emptyMap());
        }

        @Override
        public String toString() {
            return "Route [path=" + path + ", method=" + method + ", accept=" + accept + ", contentType="
                    + contentType + ", headers=" + headers + "]";
        }
    }

    class RouteBuilder {

        private Handler handler;
        private String path;
        private String method;
        private String accept;
        private String contentType;
        private Map<String, String> headers;

        private RouteBuilder() {
        }

        public static RouteBuilder newRoute() {
            return new RouteBuilder();
        }

        public static Route create(Handler handler, String path, String method, String accept, String contentType, Map<String, String> headers) {
            return newRoute().handler(handler).path(path).method(method).accept(accept).contentType(contentType).headers(() -> headers).build();
        }

        public static Route create(Handler handler, String path, String method, String accept, String contentType) {
            return create(handler, path, method, accept, contentType, Collections.emptyMap());
        }

        public static Route create(Handler handler, String path, String method, String accept) {
            return create(handler, path, method, accept, null, Collections.emptyMap());
        }

        public static Route create(Handler handler, String path, String method) {
            return create(handler, path, method, null, null, Collections.emptyMap());
        }

        public RouteBuilder handler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public RouteBuilder path(String path) {
            this.path = path;
            return this;
        }

        public RouteBuilder method(String method) {
            this.method = method;
            return this;
        }

        public RouteBuilder accept(String accept) {
            this.accept = accept;
            return this;
        }

        public RouteBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public RouteBuilder headers(Supplier<Map<String, String>> supplier) {
            this.headers = supplier.get();
            return this;
        }

        public Route build() {
            return new Route(handler,
                    path,
                    method,
                    accept = accept != null && accept.trim().length() > 0 ? accept : null,
                    contentType != null && contentType.trim().length() > 0 ? contentType : null,
                    headers);
        }
    }
}
