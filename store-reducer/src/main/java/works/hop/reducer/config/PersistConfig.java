package works.hop.reducer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;

@Configuration
@ComponentScan("works.hop.reducer")
@PropertySource("jdbc.properties")
public class PersistConfig {

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
