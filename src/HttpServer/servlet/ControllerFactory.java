package HttpServer.servlet;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerFactory {

    private static final ControllerFactory cf = new ControllerFactory();

    public static ControllerFactory getInstance() {
        return cf;
    }

    private final Map<Class<?>, Object> instanceMap = new ConcurrentHashMap<>();

    private ControllerFactory() {}

    public <T> void addController(Class<T> cls)
            throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        assert cls != null;
        if(!instanceMap.containsKey(cls)) {
            T instance = cls.getConstructor().newInstance();
            instanceMap.put(cls, instance);
        }
    }

    public synchronized <T> T getController(Class<T> cls) {
        assert cls != null;
        return cls.cast(instanceMap.get(cls));
    }

}
