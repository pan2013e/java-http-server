package HttpServer.servlet;

import HttpServer.protocol.HttpMethod;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DispatcherServlet {

    private static class URITuple {
        public String URI;
        public HttpMethod method;

        public URITuple(String URI, HttpMethod method) {
            assert URI != null && method != null;
            this.URI = URI;
            this.method = method;
        }

        @Override
        public String toString() {
            return method + " " + URI;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof URITuple _rhs)) {
                return false;
            }
            return this.URI.equals(_rhs.URI) && this.method.equals(_rhs.method);
        }

        @Override
        public int hashCode() {
            return 31 + 31 * (31 + method.hashCode()) + URI.hashCode();
        }
    }

    /* map: <URI, httpMethod> -> Java method */
    private static final Map<URITuple, Method> map = new ConcurrentHashMap<>();

    public static void add(String uri, HttpMethod httpMethod, Method method) {
        map.put(new URITuple(uri, httpMethod), method);
    }

    public static Method get(String uri, HttpMethod httpMethod) {
        return map.getOrDefault(new URITuple(uri, httpMethod), null);
    }

}
