package works.hop.jetty.demos;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class HelloServletListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("X-Init", "true");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
