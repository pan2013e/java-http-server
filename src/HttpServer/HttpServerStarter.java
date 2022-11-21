package HttpServer;

import HttpServer.annotation.Controller;
import HttpServer.annotation.EnableStaticResource;
import HttpServer.annotation.HttpApplication;
import HttpServer.configuration.MappingScanner;
import HttpServer.configuration.PackageScanner;
import HttpServer.mvc.TemplateFactory;
import HttpServer.network.Server;
import HttpServer.servlet.ControllerFactory;
import HttpServer.servlet.StaticResourceHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Logger;

public class HttpServerStarter {

    private static final Logger logger = Logger.getLogger(HttpServerStarter.class.getName());

    private static final ClassLoader cl = ClassLoader.getSystemClassLoader();

    public static void main(String[] args) {
        System.out.println("Usage: On your WebApp's main class, first add @HttpApplication, \n" +
                "then in your main function, pass main class to HttpServerStater.run() and invoke it.");
    }

    public static void run(Class<?> cls, String[] args) {
        if(!cls.isAnnotationPresent(HttpApplication.class)) {
            logger.warning("Require @HttpApplication on main class");
            System.exit(1);
        }
        HttpApplication httpApp = cls.getAnnotation(HttpApplication.class);
        assert httpApp != null;
        try {
            PackageScanner packageScanner = new PackageScanner(cls, Controller.class);
            packageScanner.getClasses().forEach(_cls -> {
                try {
                    ControllerFactory.getInstance().addController(_cls);
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                    System.exit(1);
                }
                new MappingScanner(_cls.getAnnotation(Controller.class).value()).scan(_cls);
            });
            URL resourceDir;
            if(packageScanner.getPackageName().equals("")) {
                resourceDir = cl.getResource(httpApp.resourceDir());
            } else {
                resourceDir = cl.getResource(
                        packageScanner.getPackageName().replace(".", File.separator)
                                + "/" + httpApp.resourceDir());
            }
            assert resourceDir != null && resourceDir.getProtocol().equals("file");
            String resourceStr = URLDecoder.decode(resourceDir.getPath(), StandardCharsets.UTF_8);
            TemplateFactory.getInstance().setTemplateDir(resourceStr);
            if(cls.isAnnotationPresent(EnableStaticResource.class)) {
                EnableStaticResource staticResource = cls.getAnnotation(EnableStaticResource.class);
                StaticResourceHandler.getInstance().setUriBase(staticResource.value());
                StaticResourceHandler.getInstance().setFsBase(resourceStr);
                Arrays.stream(staticResource.allow())
                        .forEach(rule -> StaticResourceHandler.getInstance().addRule(rule));
            }
        } catch (IOException e) {
            logger.warning(e.getMessage());
            System.exit(1);
        }
        Server httpServer = new Server(httpApp.addr(), httpApp.port());
        logger.info(String.format("Server listening on port %d.", httpApp.port()));
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    logger.info("Shutting down server.");
                    httpServer.close();
                })
        );
        httpServer.listen();
        logger.info("Server closed.");
    }

}
