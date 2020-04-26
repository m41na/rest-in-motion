package works.hop.reducer.config;

import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.sql.SQLException;

@Configuration
@ComponentScan(basePackages = "works.hop.reducer", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = PersistConfig.class))
@PropertySource("jdbc-test.properties")
public class PersistTestConfig {

    @Autowired
    Environment env;
    @Value("${jdbc.url}")
    String url;
    @Value("${jdbc.username}")
    String user;
    @Value("${jdbc.password}")
    String pass;
    @Value("${jdbc.driverClassName}")
    String driver;
    @Value("${local.jdbc.url}")
    String localUrl;
    @Value("${local.jdbc.username}")
    String localUser;
    @Value("${local.jdbc.password}")
    String localPass;

    @PostConstruct
    public void setupScript() throws SQLException {
        Path path = Path.of(System.getProperty("user.dir"), "src/test/resources", "schema.sql");
        RunScript.execute(localUrl, localUser, localPass, path.toString(), Charset.defaultCharset(), false);
    }

    @Bean(name = "remoteDS")
    public DataSource remoteDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        return dataSource;
    }

    @Bean(name = "localDS")
    public DataSource localDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(localUrl);
        dataSource.setUsername(localUser);
        dataSource.setPassword(localPass);
        return dataSource;
    }

    @Bean
    public JedisPool redisClient(@Value("${redis.port}") int port, @Value("${redis.host}") String host) {
        return new JedisPool(host, port);
    }
}
