package works.hop.jetty.startup;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.util.function.Function;

import static org.eclipse.jetty.util.resource.Resource.newClassPathResource;

public class AppConnectors {

    private AppConnectors() {
        throw new UnsupportedOperationException("You should not instantiate this class");
    }

    public static HttpConfiguration createHttpConfiguration(Function<String, String> properties) {
        Integer securePort = Integer.parseInt(properties.apply("https.port"));
        Long idleTimeOut = Long.parseLong(properties.apply("https.idleTimeout"));
        // HTTP Configuration
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(securePort);
        http_config.setIdleTimeout(idleTimeOut);
        http_config.setOutputBufferSize(Integer.parseInt(properties.apply("https.outputBufferSize")));
        return http_config;
    }

    public static SecureRequestCustomizer createHttpsCustomizer(Function<String, String> properties) {
        SecureRequestCustomizer customizer = new SecureRequestCustomizer();
        customizer.setStsMaxAge(Long.parseLong(properties.apply("https.ssl.stsMaxAge")));
        customizer.setStsIncludeSubDomains(Boolean.parseBoolean(properties.apply("https.ssl.includeSubDomains")));
        return customizer;
    }

    public static ServerConnector configureHttpConnector(Function<String, String> properties, Server server, String host, Integer port, HttpConfiguration http_config) {
        //add http connector (http_1.1 connection factory)
        ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(http_config));
        httpConnector.setHost(host);
        httpConnector.setPort(port);
        httpConnector.setIdleTimeout(Long.parseLong(properties.apply("https.idleTimeout"))); //milliseconds
        return httpConnector;
    }

    public static ServerConnector configureHttpsConnector(Function<String, String> properties, Server server, String host, Integer port, HttpConfiguration http_config) {
        //add http connector (http_1.1 connection factory)
        ServerConnector httpConnector = configureHttpConnector(properties, server, host, port, http_config);
        server.addConnector(httpConnector);

        // HTTPS Configuration
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        SecureRequestCustomizer customizer = createHttpsCustomizer(properties);
        https_config.addCustomizer(customizer);

        // configure alpn connection factory for http2
        ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        alpn.setDefaultProtocol("h2");

        // Configure ssl context factory
        SslContextFactory sslContextFactory = createSslContextFactory(
                properties.apply("https.keystore.classpath"),
                properties.apply("https.keystore.password")
        );

        // SSL Connection Factory
        SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

        // add https connector (both http_1.1 and http_2 connection factory)
        ServerConnector http2Connector = new ServerConnector(server, ssl, alpn,
                new HTTP2ServerConnectionFactory(https_config),
                new HttpConnectionFactory(https_config));
        http2Connector.setPort(Integer.parseInt(properties.apply("https.port")));
        http2Connector.setIdleTimeout(Long.parseLong(properties.apply("https.idleTimeout"))); //milliseconds
        return http2Connector;
    }

    private static SslContextFactory createSslContextFactory(String keyFile, String keyPass) {
        // SSL Context Factory for HTTPS and HTTP/2
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStoreResource(newClassPathResource(keyFile));
        sslContextFactory.setKeyStorePassword(keyPass);
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        return sslContextFactory;
    }
}
