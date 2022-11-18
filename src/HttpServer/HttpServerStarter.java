package HttpServer;

import HttpServer.annotation.Controller;
import HttpServer.annotation.HttpApplication;
import HttpServer.configuration.MappingScanner;
import HttpServer.configuration.PackageScanner;
import HttpServer.mvc.TemplateFactory;
import HttpServer.network.Server;
import HttpServer.servlet.ControllerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class HttpServerStarter {

    private static final Logger logger = Logger.getLogger(HttpServerStarter.class.getName());

    public static void main(String[] args) throws Throwable {
        if(args.length < 1) {
            System.out.println("Usage: java HttpServerStarter.main ClassName");
            System.exit(1);
        }
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Class<?> mc = cl.loadClass(args[0]);
        mc.getMethod("main", String[].class).invoke(null, args);
    }

    public static void run(Class<?> cls, String[] args) {
        if(!cls.isAnnotationPresent(HttpApplication.class)) {
            logger.warning("Require @HttpApplication on starter class");
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
                resourceDir = ClassLoader.getSystemClassLoader()
                        .getResource(httpApp.resourceDir());
            } else {
                resourceDir = ClassLoader.getSystemClassLoader()
                        .getResource(packageScanner.getPackageName().replace(".", File.separator)
                                + "/" + httpApp.resourceDir());
            }
            assert resourceDir != null && resourceDir.getProtocol().equals("file");
            TemplateFactory.getInstance()
                    .setTemplateDir(URLDecoder.decode(resourceDir.getPath(), StandardCharsets.UTF_8));
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
