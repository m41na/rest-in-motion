package works.hop.route;

import com.fasterxml.jackson.annotation.JsonIgnore;
import works.hop.handler.HandlerChain;
import works.hop.handler.HandlerFunction;
import works.hop.handler.impl.DefaultHandlerChain;
import works.hop.traverse.Visitable;
import works.hop.traverse.Visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;

public interface Routing {

    enum RouteType {BEFORE, HANDLE, AFTER}

    interface Searchable<S, E> {

        void search(S criteria);

        boolean contains(S criteria);

        void info(List<String> nodes, String prefix);

        void add(E route);

        void remove(E route);
    }

    interface Router extends Searchable<Search, Route>, Visitable {

        @Override
        default void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    class Search {

        //search attributes
        public final Attributes attributes;
        //result attributes
        public final Map<String, String> pathParams = new HashMap<>();
        @JsonIgnore
        public Route route;
        @JsonIgnore
        public HandlerChain chain = new DefaultHandlerChain();

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

    class Route implements Visitable {

        public final RouteType type;
        public final String path;
        public final String accept;
        public final String contentType;
        public final Map<String, String> headers = new HashMap<>();
        public final HandlerFunction handler;
        public String method;

        public Route(HandlerFunction handler, RouteType type, String path, String method, String accept, String contentType, Map<String, String> headers) {
            this.type = type;
            this.path = path;
            this.method = method;
            this.accept = accept;
            this.contentType = contentType;
            this.headers.putAll(headers);
            this.handler = handler;
        }

        public Route(HandlerFunction handler, RouteType type, String... args) {
            this(handler, type,
                    args.length > 0 ? args[0] : null,
                    args.length > 1 ? args[1] : null,
                    args.length > 2 ? args[2] : null,
                    args.length > 3 ? args[3] : null,
                    emptyMap());
        }

        public Route(HandlerFunction handler, String path, String method, String accept, String contentType) {
            this(handler,
                    RouteType.HANDLE,
                    path,
                    method,
                    accept,
                    contentType,
                    emptyMap());
        }

        @Override
        public String toString() {
            return "Route [path=" + path + ", method=" + method + ", accept=" + accept + ", contentType="
                    + contentType + ", headers=" + headers + "]";
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    class RouteBuilder {

        private HandlerFunction handler;
        private RouteType type;
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

        public static Route create(HandlerFunction handler, String path, String method, String accept, String contentType, Map<String, String> headers) {
            return newRoute().handler(handler).path(path).method(method).accept(accept).contentType(contentType).headers(() -> headers).build();
        }

        public static Route create(HandlerFunction handler, RouteType type, String path, String method, String accept, String contentType) {
            return newRoute().type(type).handler(handler).path(path).method(method).accept(accept).contentType(contentType).headers(() -> emptyMap()).build();
        }

        public static Route create(HandlerFunction handler, String path, String method, String accept, String contentType) {
            return create(handler, path, method, accept, contentType, emptyMap());
        }

        public static Route create(HandlerFunction handler, RouteType type, String path, String method, String accept) {
            return create(handler, type, path, method, accept, null);
        }

        public static Route create(HandlerFunction handler, String path, String method, String accept) {
            return create(handler, path, method, accept, null, emptyMap());
        }

        public static Route create(HandlerFunction handler, RouteType type, String path, String method) {
            return create(handler, type, path, method, null, null);
        }

        public static Route create(HandlerFunction handler, String path, String method) {
            return create(handler, path, method, null, null, emptyMap());
        }

        public RouteBuilder handler(HandlerFunction handler) {
            this.handler = handler;
            return this;
        }

        public RouteBuilder type(RouteType type) {
            this.type = type;
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
                    type != null ? type : RouteType.HANDLE,
                    path,
                    method,
                    accept = accept != null && accept.trim().length() > 0 ? accept : null,
                    contentType != null && contentType.trim().length() > 0 ? contentType : null,
                    headers != null ? headers : emptyMap());
        }
    }
}
