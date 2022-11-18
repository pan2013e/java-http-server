package HttpServer;

import HttpServer.annotation.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static final String SERVER_NAME = "JavaHttpServer";

    public static String getQueryParameterName(Method method, int index) {
        Annotation[] parameterAnnots = method.getParameterAnnotations()[index];
        if(parameterAnnots == null || parameterAnnots.length == 0) {
            return null;
        }
        for(var annotation : parameterAnnots) {
            if(annotation instanceof Query query) {
                return query.value();
            }
        }
        return null;
    }

    public static String uriConcat(String base, String more) {
        assert base != null && more != null;
        assert base.startsWith("/");
        if(more.equals("/")) {
            return base;
        }
        if(base.endsWith("/") && more.startsWith("/")) {
            return base + more.substring(1);
        } else if( (!base.endsWith("/") && more.startsWith("/")) ||
                (base.endsWith("/") && !more.startsWith("/"))) {
            return base + more;
        } else {
            return base + "/" + more;
        }
    }

    public static boolean isToStringOverriden(Object o) {
        try {
            return o.getClass().getMethod("toString").getDeclaringClass() != Object.class;
        } catch (NoSuchMethodException ignored){
            System.out.println("Impossible to reach here");
            return false;
        }
    }

    public static String getServerTime() {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(
                ZonedDateTime.now(ZoneOffset.UTC)
        );
    }

}
