package works.hop.jetty.servlet;

import org.eclipse.jetty.servlet.ServletHolder;

public interface ServletConfig {

    void configure(ServletHolder holder);
}
