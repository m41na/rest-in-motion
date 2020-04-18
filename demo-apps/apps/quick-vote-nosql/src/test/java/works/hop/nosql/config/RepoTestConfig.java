package works.hop.nosql.config;

import com.arangodb.ArangoDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
@PropertySource("nosql.test.db.properties")
@ComponentScan(basePackages = {"works.hop.nosql.repo", "works.hop.nosql.service"})
public class RepoTestConfig {

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() {
        System.out.println("initializing spring repo context");
    }

    @Bean
    public ArangoDB arangoDB(@Value("${db.name}") String dbName) {
        ArangoDB arangoDB = new ArangoDB.Builder()
                .user(env.getProperty("db.user"))
                .password(env.getProperty("db.password")).build();
        if (arangoDB.getDatabases().isEmpty()) {
            arangoDB.createDatabase(dbName);
        }
        return arangoDB;
    }

    @PreDestroy
    public void destroy() {
        System.out.println("destroying spring repo context");
        arangoDB(env.getProperty("dn.name")).shutdown();
    }
}


