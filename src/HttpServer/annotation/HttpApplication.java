package HttpServer.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HttpApplication {
    byte[] addr() default {0, 0, 0, 0};
    int port() default 8080;
    String resourceDir() default "resources";
}
