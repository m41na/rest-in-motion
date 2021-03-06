package works.hop.jetty.startup;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.eclipse.jetty.servlets.CrossOriginFilter.*;

public class AppCorsFilter {

    private AppCorsFilter() {
        throw new UnsupportedOperationException("You should not instantiate this class");
    }

    public static FilterHolder configCorsFilter(Map<String, String> corsContext) {
        FilterHolder corsFilter = new FilterHolder(CrossOriginFilter.class);
        //add default values
        corsFilter.setInitParameter(ALLOWED_ORIGINS_PARAM, Optional.ofNullable(corsContext.get(ALLOWED_ORIGINS_PARAM)).orElse("*"));
        corsFilter.setInitParameter(ALLOWED_METHODS_PARAM, Optional.ofNullable(corsContext.get(ALLOWED_METHODS_PARAM)).orElse("GET,POST,PUT,DELETE,OPTIONS,HEAD"));
        corsFilter.setInitParameter(ALLOWED_HEADERS_PARAM, Optional.ofNullable(corsContext.get(ALLOWED_HEADERS_PARAM)).orElse("Content-Type,Accept,Origin"));
        corsFilter.setInitParameter(ALLOW_CREDENTIALS_PARAM, Optional.ofNullable(corsContext.get(ALLOW_CREDENTIALS_PARAM)).orElse("true"));
        corsFilter.setInitParameter(PREFLIGHT_MAX_AGE_PARAM, Optional.ofNullable(corsContext.get(PREFLIGHT_MAX_AGE_PARAM)).orElse("728000"));
        //add other user defined values that are not in the list of default keys
        List<String> skipKeys = Arrays.asList(
                ALLOWED_ORIGINS_PARAM,
                ALLOWED_METHODS_PARAM,
                ALLOWED_HEADERS_PARAM,
                ALLOW_CREDENTIALS_PARAM,
                PREFLIGHT_MAX_AGE_PARAM);
        corsContext.keySet().stream().filter(key -> !skipKeys.contains(key)).forEach(key -> corsFilter.setInitParameter(key, corsContext.get(key)));
        corsFilter.setName("rim-cors-filter");

        FilterMapping corsMapping = new FilterMapping();
        corsMapping.setFilterName("cross-origin");
        corsMapping.setPathSpec("*");
        return corsFilter;
    }
}
