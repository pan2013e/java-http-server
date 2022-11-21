package HttpServer.servlet;

import HttpServer.protocol.HttpMethod;
import com.yevdo.jwildcard.JWildcard;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class DispatcherServlet {

    @Getter
    private static class URITuple {
        private final String URI;
        private final HttpMethod method;

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

    /* map: URITuple.hashCode() -> Java method */
    private static final Map<Integer, Method> map = new ConcurrentHashMap<>();

    /* set: rules<URI regex string, HttpMethod>*/
    private static final Set<URITuple> ruleSet = new CopyOnWriteArraySet<>();

    public synchronized static void add(String wildcard, HttpMethod httpMethod, Method method) {
        URITuple uriTuple = new URITuple(JWildcard.wildcardToRegex(wildcard), httpMethod);
        ruleSet.add(uriTuple);
        map.put(uriTuple.hashCode(), method);
    }

    public synchronized static Method get(String uri, HttpMethod httpMethod) {
        if(uri == null || httpMethod == null) {
            return null;
        }
        List<URITuple> res = ruleSet.stream()
                                .filter(tuple -> tuple.getMethod().equals(httpMethod))
                                .filter(tuple -> uri.matches(tuple.getURI())).toList();
        if(res.size() == 0) {
            return null;
        } else {
            return map.getOrDefault(res.get(0).hashCode(), null);
        }
    }

}
