package HttpServer;

import HttpServer.annotation.Controller;
import HttpServer.annotation.HttpApplication;
import HttpServer.configuration.MappingScanner;
import HttpServer.configuration.PackageScanner;
import HttpServer.network.Server;
import HttpServer.servlet.DispatcherServlet;

import java.io.IOException;
import java.util.logging.Logger;

public class HttpServerStarter {

    private static final Logger logger = Logger.getLogger(HttpServerStarter.class.getName());

    public static void start(Class<?> cls) {
        if(!cls.isAnnotationPresent(HttpApplication.class)) {
            logger.warning("Require @HttpApplication on starter class");
            System.exit(1);
        }
        HttpApplication httpApp = cls.getAnnotation(HttpApplication.class);
        assert httpApp != null;
        try {
            PackageScanner packageScanner = new PackageScanner(cls, Controller.class);
            packageScanner.getClasses().forEach(_cls -> {
                Controller controller = _cls.getAnnotation(Controller.class);
                new MappingScanner(controller.value()).scan(_cls);
            });
        } catch (IOException e) {
            logger.warning(e.getMessage());
            System.exit(1);
        }
        DispatcherServlet.setResourceDir(httpApp.resourceDir());
        Server httpServer = new Server(httpApp.addr(), httpApp.port());
        logger.info(String.format("Server listening on port %d.", httpApp.port()));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down server.");
            httpServer.close();
        }));
        httpServer.listen();
        logger.info("Server closed.");
    }

}
