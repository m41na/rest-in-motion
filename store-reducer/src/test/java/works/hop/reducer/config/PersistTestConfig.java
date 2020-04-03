package works.hop.reducer.config;

import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.sql.SQLException;

@Configuration
@ComponentScan("works.hop.reducer")
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

    @PostConstruct
    public void setupScript() throws SQLException {
        Path path = Path.of(System.getProperty("user.dir"), "/src/test/resources", "schema.sql");
        RunScript.execute(url, user, pass, path.toString(), Charset.defaultCharset(), false);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        return dataSource;
    }

    @Bean
    public Jedis redisClient() {
        return new Jedis();
    }
}
