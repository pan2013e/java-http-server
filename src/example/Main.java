package example;

import HttpServer.annotation.HttpApplication;
import HttpServer.HttpServerStarter;

@HttpApplication
public class Main {

    public static void main(String[] args) {
        HttpServerStarter.start(Main.class);
    }

}