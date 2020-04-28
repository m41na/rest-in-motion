package works.hop.scrum.config;

import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.sql.SQLException;

@Configuration
@ComponentScans(value = {@ComponentScan("works.hop.scrum.repo"), @ComponentScan("works.hop.scrum.service")})
@PropertySource("jdbc-test.properties")
public class RepoConfigTest {

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
        Path path = Path.of(System.getProperty("user.dir"), "src/test/resources", "schema.sql");
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
}
