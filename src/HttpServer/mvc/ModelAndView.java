package HttpServer.mvc;

import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModelAndView {

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private String viewName = "";

    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Object> map = new HashMap<>();

    public void addObject(String key, Object value) {
        if(key != null) {
            map.put(key, Objects.requireNonNullElse(value, ""));
        }
    }

    public void removeObject(String key) {
        if(key != null) {
            map.remove(key);
        }
    }

}
