package example;

import HttpServer.annotation.EnableStaticResource;
import HttpServer.annotation.HttpApplication;
import HttpServer.HttpServerStarter;

@HttpApplication
@EnableStaticResource
public class Main {

    public static void main(String[] args) {
        HttpServerStarter.run(Main.class, args);
    }

}