package HttpServer;

import HttpServer.annotation.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class Utils {

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
}
