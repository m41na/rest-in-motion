package works.hop.scrum.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScans(value = {@ComponentScan("works.hop.scrum.repo"), @ComponentScan("works.hop.scrum.service")})
@PropertySource("jdbc.properties")
public class RepoConfig {

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
