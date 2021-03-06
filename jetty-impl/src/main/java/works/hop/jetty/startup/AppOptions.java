package works.hop.jetty.startup;

import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppOptions {

    private AppOptions() {
        throw new UnsupportedOperationException("You should not instantiate this class. It's intended to be used as a utility class");
    }

    private static Properties loadProperties(String protocol, String host, String jarFile, String propsFile) {
        Properties props = new Properties();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(Paths.get(propsFile).toFile()))) {
            props.load(reader);
        } catch (Exception e1) {
            try (InputStreamReader reader = new InputStreamReader(AppOptions.class.getResourceAsStream(propsFile))) {
                props.load(reader);
            } catch (Exception e2) {
                try (InputStreamReader reader = new InputStreamReader(AppOptions.class.getClassLoader().getResourceAsStream(propsFile))) {
                    props.load(reader);
                } catch (Exception e3) {
                    try {
                        URL jarURL = new URL(protocol, host, jarFile);
                        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{jarURL});
                        try (InputStream reader = urlClassLoader.getResourceAsStream(propsFile)) {
                            props.load(reader);
                        } catch (IOException e4) {
                            System.err.printf("Could not locate file to load properties; %s", e3.getMessage());
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return props;
    }

    private static Properties loadProperties(String dataFile) {
        return loadProperties(null, null, null, dataFile);
    }

    private static Properties handleCli(Options options, String[] args) {
        //define acceptable options
        options.addOption("p", "port", true, "The location to load static content from, if it is required")
                .addOption("h", "host", true, "The application's entry point from the URL")
                .addOption("n", "name", true, "A unique name for the application for configurability")
                .addOption("c", "config", true, "The application's config properties file")

                .addOption("appctx", true, "The application's entry point from the URL")
                .addOption("templates", true, "The base location from which to look up content templates")
                .addOption("engine", true, "The application to use for interpreting the content templates")
                .addOption("lookup", true, "The strategy which the application should use to load content templates")
                .addOption("splash", true, "A file whose content is used for the banner")

                .addOption("sessionJdbcEnable", false, "Enable using a jdbc-backed session")
                .addOption("sessionJdbcUrl", true, "The jdbc url to the session database")
                .addOption("sessionJdbcDriver", true, "The jdbc driver to the session database")

                .addOption("assetsDirAllowed", true, "Allow the application to server the static content folder")
                .addOption("assetsPathInfoOnly", true, "Set the default servlet to use the path info only for resolving content lookup")
                .addOption("assetsWelcomeFile", true, "Name of static resource file to serve when no resource name is provided")
                .addOption("assetsAcceptRanges", true, "Set accept ranges attribute for the default servlet")
                .addOption("assetsEtags", true, "Set the etags attribute for the default servlet")
                .addOption("assetsCacheControl", true, "Set the cache control header the default servlet should use")

                .addOption("poolSize", true, "The initial size of the server's thread pool")
                .addOption("maxPoolSize", true, "The maximum size of the server's thread pool")
                .addOption("keepAliveTime", true, "The server's duration for keeping a thread alive")

                .addOption("httpsPort", true, "The server's secure https port")
                .addOption("httpsIdleTimeout", true, "The server's duration to keep an idle connection alive")
                .addOption("httpsOutputBufferSize", true, "The server buffer size for holding response content before flushing")
                .addOption("httpsSslStsMaxAge", true, "The base location from which to look up content templates")
                .addOption("httpsSslIncludeSubDomains", true, "Use secure connections fro sub-domains")
                .addOption("httpsKeystoreClasspath", true, "The classpath location of the keystore file")
                .addOption("httpsKeystorePassword", true, "The keystore's password")

                .addOption("fcgi", true, "Activate fcgi servlet context handler");

        Properties props = new Properties();
        try {
            //get command line props, which will be preferred over default value
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("config")) {
                Properties extraProperties = loadProperties(cmd.getOptionValue("config"));
                //override any property whose command line argument is available
                for (Map.Entry<Object, Object> entry : extraProperties.entrySet()) {
                    String key = String.valueOf(entry.getKey());
                    if (cmd.hasOption(key)) {
                        props.setProperty(key, cmd.getOptionValue(key));
                    } else {
                        props.setProperty(key, String.valueOf(entry.getValue()));
                    }
                }
            }
            if (cmd.hasOption("port")) {
                props.setProperty("port", cmd.getOptionValue("port"));
            }
            if (cmd.hasOption("host")) {
                props.setProperty("host", cmd.getOptionValue("host"));
            }
            if (cmd.hasOption("name")) {
                props.setProperty("name", cmd.getOptionValue("name"));
            }

            if (cmd.hasOption("appctx")) {
                props.setProperty("appctx", cmd.getOptionValue("appctx"));
            }
            if (cmd.hasOption("templates")) {
                props.setProperty("templates", cmd.getOptionValue("templates"));
            }
            if (cmd.hasOption("engine")) {
                props.setProperty("engine", cmd.getOptionValue("engine"));
            }
            if (cmd.hasOption("lookup")) {
                props.setProperty("lookup", cmd.getOptionValue("lookup"));
            }
            if (cmd.hasOption("splash")) {
                props.setProperty("splash", cmd.getOptionValue("splash"));
            }

            if (cmd.hasOption("sessionJdbcEnable")) {
                props.setProperty("session.jdbc.enable", cmd.getOptionValue("sessionJdbcEnable"));
            }
            if (cmd.hasOption("sessionJdbcUrl")) {
                props.setProperty("session.jdbc.url", cmd.getOptionValue("sessionJdbcUrl"));
            }
            if (cmd.hasOption("sessionJdbcDriver")) {
                props.setProperty("session.jdbc.driver", cmd.getOptionValue("sessionJdbcDriver"));
            }

            if (cmd.hasOption("assetsDirAllowed")) {
                props.setProperty("assets.dirAllowed", cmd.getOptionValue("assetsDirAllowed"));
            }
            if (cmd.hasOption("assetsPathInfoOnly")) {
                props.setProperty("assets.pathInfoOnly", cmd.getOptionValue("assetsPathInfoOnly"));
            }
            if (cmd.hasOption("assetsWelcomeFile")) {
                props.setProperty("assets.welcomeFile", cmd.getOptionValue("assetsWelcomeFile"));
            }
            if (cmd.hasOption("assetsAcceptRanges")) {
                props.setProperty("assets.acceptRanges", cmd.getOptionValue("assetsAcceptRanges"));
            }
            if (cmd.hasOption("assetsEtags")) {
                props.setProperty("assets.etags", cmd.getOptionValue("assetsEtags"));
            }
            if (cmd.hasOption("assetsCacheControl")) {
                props.setProperty("assets.cacheControl", cmd.getOptionValue("assetsCacheControl"));
            }

            if (cmd.hasOption("poolSize")) {
                props.setProperty("poolSize", cmd.getOptionValue("poolSize"));
            }
            if (cmd.hasOption("maxPoolSize")) {
                props.setProperty("maxPoolSize", cmd.getOptionValue("maxPoolSize"));
            }
            if (cmd.hasOption("keepAliveTime")) {
                props.setProperty("keepAliveTime", cmd.getOptionValue("keepAliveTime"));
            }

            if (cmd.hasOption("httpsPort")) {
                props.setProperty("https.port", cmd.getOptionValue("httpsPort"));
            }
            if (cmd.hasOption("httpsIdleTimeout")) {
                props.setProperty("https.idleTimeout", cmd.getOptionValue("httpsIdleTimeout"));
            }
            if (cmd.hasOption("httpsOutputBufferSize")) {
                props.setProperty("https.outputBufferSize", cmd.getOptionValue("httpsOutputBufferSize"));
            }
            if (cmd.hasOption("httpsSslStsMaxAge")) {
                props.setProperty("https.ssl.stsMaxAge", cmd.getOptionValue("httpsSslStsMaxAge"));
            }
            if (cmd.hasOption("httpsSslIncludeSubDomains")) {
                props.setProperty("https.ssl.includeSubDomains", cmd.getOptionValue("httpsSslIncludeSubDomains"));
            }
            if (cmd.hasOption("httpsKeystoreClasspath")) {
                props.setProperty("https.keystore.classpath", cmd.getOptionValue("httpsKeystoreClasspath"));
            }
            if (cmd.hasOption("httpsKeystorePassword")) {
                props.setProperty("https.keystore.password", cmd.getOptionValue("httpsKeystorePassword"));
            }

            if (cmd.hasOption("fcgi")) {
                props.setProperty("fcgi", cmd.getOptionValue("fcgi"));
            }
        } catch (Exception e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("AppOptions CLI", options);
        }
        return props;
    }

    public static Map<String, String> applyDefaults(Options options, String[] args) {
        //gather properties info
        Properties props = handleCli(options, args);
        //add all properties to map before applying defaults to missing values
        Stream<Map.Entry<Object, Object>> stream = props.entrySet().stream();
        Map<String, String> properties = stream.collect(Collectors.toMap(
                e -> String.valueOf(e.getKey()),
                e -> String.valueOf(e.getValue())));
        //protocol
        properties.put("port", Optional.ofNullable(props.getProperty("port")).orElse("8080"));
        properties.put("host", Optional.ofNullable(props.getProperty("host")).orElse("localhost"));
        properties.put("name", Optional.ofNullable(props.getProperty("name")).orElse(UUID.randomUUID().toString()));
        //basic
        properties.put("appctx", Optional.ofNullable(props.getProperty("appctx")).orElse("/"));
        properties.put("templates", Optional.ofNullable(props.getProperty("templates")).orElse("templates/"));
        properties.put("engine", Optional.ofNullable(props.getProperty("engine")).orElse("*"));
        properties.put("lookup", Optional.ofNullable(props.getProperty("lookup")).orElse("FILE"));
        properties.put("splash", Optional.ofNullable(props.getProperty("splash")).orElse("/splash/shadow.txt"));
        //http session
        properties.put("session.jdbc.enable", Optional.ofNullable(props.getProperty("session.jdbc.enable")).orElse(
                Optional.ofNullable(props.getProperty("sessionJdbcEnable")).orElse("false")));
        properties.put("session.jdbc.url", Optional.ofNullable(props.getProperty("session.jdbc.url")).orElse(
                Optional.ofNullable(props.getProperty("sessionJdbcUrl")).orElse(String.format("%s%s%s", "jdbc:h2:~/", properties.get("name"), "-session"))));
        properties.put("session.jdbc.driver", Optional.ofNullable(props.getProperty("session.jdbc.driver")).orElse(
                Optional.ofNullable(props.getProperty("sessionJdbcDriver")).orElse("org.h2.Driver")));
        //static resources
        properties.put("assets.dirAllowed", Optional.ofNullable(props.getProperty("assets.dirAllowed")).orElse(
                Optional.ofNullable(props.getProperty("assetsDirAllowed")).orElse("false")));
        properties.put("assets.pathInfoOnly", Optional.ofNullable(props.getProperty("assets.pathInfoOnly")).orElse(
                Optional.ofNullable(props.getProperty("assetsPathInfoOnly")).orElse("true")));
        properties.put("assets.welcomeFile", Optional.ofNullable(props.getProperty("assets.welcomeFile")).orElse(
                Optional.ofNullable(props.getProperty("assetsWelcomeFile")).orElse("index.html")));
        properties.put("assets.acceptRanges", Optional.ofNullable(props.getProperty("assets.acceptRanges")).orElse(
                Optional.ofNullable(props.getProperty("assetsAcceptRanges")).orElse("true")));
        properties.put("assets.etags", Optional.ofNullable(props.getProperty("assets.etags")).orElse(
                Optional.ofNullable(props.getProperty("assetsEtags")).orElse("true")));
        properties.put("assets.cacheControl", Optional.ofNullable(props.getProperty("assets.cacheControl")).orElse(
                Optional.ofNullable(props.getProperty("assetsCacheControl")).orElse("public, max-age=0")));
        //thread pool
        properties.put("poolSize", Optional.ofNullable(props.getProperty("poolSize")).orElse("5"));
        properties.put("maxPoolSize", Optional.ofNullable(props.getProperty("maxPoolSize")).orElse("200"));
        properties.put("keepAliveTime", Optional.ofNullable(props.getProperty("keepAliveTime")).orElse("30000"));
        //https
        properties.put("https.port", Optional.ofNullable(props.getProperty("https.port")).orElse(
                Optional.ofNullable(props.getProperty("httpsPort")).orElse("8443")));
        properties.put("https.idleTimeout", Optional.ofNullable(props.getProperty("https.idleTimeout")).orElse(
                Optional.ofNullable(props.getProperty("httpsIdleTimeout")).orElse("30000")));
        properties.put("https.outputBufferSize", Optional.ofNullable(props.getProperty("https.outputBufferSize")).orElse(
                Optional.ofNullable(props.getProperty("httpsOutputBufferSize")).orElse("32768")));
        properties.put("https.ssl.stsMaxAge", Optional.ofNullable(props.getProperty("https.ssl.stsMaxAge")).orElse(
                Optional.ofNullable(props.getProperty("httpsSslStsMaxAge")).orElse("2000")));
        properties.put("https.ssl.includeSubDomains", Optional.ofNullable(props.getProperty("https.ssl.includeSubDomains")).orElse(
                Optional.ofNullable(props.getProperty("httpsSslIncludeSubDomains")).orElse("true")));
        properties.put("https.keystore.classpath", Optional.ofNullable(props.getProperty("https.keystore.classpath")).orElse(
                Optional.ofNullable(props.getProperty("httpsKeystoreClasspath")).orElse("keystore.jks")));
        properties.put("https.keystore.password", Optional.ofNullable(props.getProperty("https.keystore.password")).orElse(
                Optional.ofNullable(props.getProperty("httpsKeystorePassword")).orElse("OBF:1x901wu01v1x20041ym71zzu1v2h1wue1x7u")));
        //bonus
        properties.put("fcgi", Optional.ofNullable(props.getProperty("fcgi")).orElse(
                Optional.ofNullable(props.getProperty("fcgi")).orElse("false")));

        return properties;
    }
}
