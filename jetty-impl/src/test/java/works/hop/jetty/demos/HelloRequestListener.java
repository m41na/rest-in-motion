package works.hop.jetty.demos;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class HelloRequestListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        sre.getServletRequest().setAttribute("X-ReqListener", "true");
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
    }
}
