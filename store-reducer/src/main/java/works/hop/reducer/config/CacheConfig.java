package works.hop.reducer.config;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public Cache cache() {
        Cache<String, String> cache = new Cache2kBuilder<String, String>() {
        }
                .name("reducer")
                .eternal(true)
                .entryCapacity(100)
                .build();
        return cache;
    }
}
