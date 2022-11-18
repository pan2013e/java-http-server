package HttpServer.configuration;

import HttpServer.Utils;
import HttpServer.annotation.GetMapping;
import HttpServer.annotation.RequestMapping;
import HttpServer.protocol.HttpMethod;
import HttpServer.servlet.DispatcherServlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MappingScanner {

    private final String baseUri;

    private static final Class<? extends Annotation>[] mappingAnnots
            = new Class[]{ RequestMapping.class, GetMapping.class };

    public MappingScanner(String baseUri) {
        assert baseUri != null;
        this.baseUri = baseUri;
    }

    public void scan(Class<?> cls) {
        Arrays.stream(cls.getDeclaredMethods()).filter(this::findAnnotation)
                .forEach(this::addMapping);
    }

    private boolean findAnnotation(Method method) {
        return Arrays.stream(mappingAnnots).anyMatch(method::isAnnotationPresent);
    }

    private void addMapping(Method method) {
        Arrays.stream(mappingAnnots)
                .filter(method::isAnnotationPresent)
                .forEach(annotClass -> {
                    Annotation annot = method.getAnnotation(annotClass);
                    try {
                        Method getValue = annotClass.getDeclaredMethod("value");
                        Method getHttpMethod = annotClass.getDeclaredMethod("method");
                        DispatcherServlet.add(Utils.uriConcat(baseUri, (String) getValue.invoke(annot)),
                                (HttpMethod) getHttpMethod.invoke(annot), method);
                    } catch (NoSuchMethodException | IllegalAccessException
                            | InvocationTargetException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                });
    }

}
