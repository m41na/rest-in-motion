package works.hop.jetty.hello;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println("<h1>Hello from HelloServlet</h1>");
    }

    //***** BEGIN EXPERIMENT *****//
//            ServletContextHandler servletsContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
//            servletsContext.setContextPath("/ser");
//            servletsContext.addServlet(HelloServlet.class, "/hello/*");
//
////            ContextHandler context = new ContextHandler("/");
////            context.setContextPath("/");
////            context.setHandler(new HelloHandler("Root Hello"));
//
//            ContextHandler contextFR = new ContextHandler("/fr");
//            contextFR.setHandler(new HelloHandler("Bonjour"));
//            ContextHandlerCollection handlerCollection = new ContextHandlerCollection(contextFR, createRoutesHandler("/wow"));
//
//            Path userDir = Paths.get(System.getProperty("user.dir"));
//            PathResource pathResource = new PathResource(userDir);
//            ResourceHandler resourceHandler = new ResourceHandler();
//
//            // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
//            // In this example it is the current directory but it can be configured to anything that the jvm has access to.
//            resourceHandler.setDirectoriesListed(true);
//            resourceHandler.setWelcomeFiles(new String[]{"index.html"});
//            resourceHandler.setBaseResource(pathResource);
//
//            // Add the ResourceHandler to the server.
//            HandlerList handlerList = new HandlerList();
//            handlerList.setHandlers(new Handler[]{resourceHandler, servletsContext, handlerCollection});
//            server.setHandler(handlerList);
//
//            route("get", "/api", "text/html", "*", (req, res, done) -> {
//                res.send("wow handler!");
//                done.complete();
//            });
    //***** END EXPERIMENT *****//
}
