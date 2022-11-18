package HttpServer.protocol;

import lombok.AccessLevel;
import lombok.Getter;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    @Getter(AccessLevel.PUBLIC)
    private HttpMethod method;

    @Getter(AccessLevel.PUBLIC)
    private String resourceURI;

    @Getter(AccessLevel.PUBLIC)
    private HttpVersion version;

    private final Map<String, String> header = new HashMap<>();

    private final Map<String, String> queries = new HashMap<>();

    public void parseLine(List<String> lines) {
        assert lines.size() >= 1;
        String firstLine = lines.get(0).trim();
        String[] arr = firstLine.split(" ");
        assert arr.length == 3;
        method = HttpMethod.valueOf(arr[0].toUpperCase());
        resourceURI = parseURI(arr[1]);
        version = HttpVersion.HTTP_1_1;
        for(int i = 1;i < lines.size();i++) {
            String[] _header = lines.get(i).split(":", 2);
            assert _header.length == 2;
            header.put(_header[0].trim(), _header[1].trim());
        }
    }

    private String parseURI(String URI) {
        String[] components = URI.split("\\?", 2);
        assert components.length <= 2;
        if(components.length == 2) {
            String[] pairs = components[1].split("&");
            for(String pair : pairs) {
                String[] tuple = pair.split("=");
                if(tuple.length < 2) {
                    continue;
                }
                queries.put(URLDecoder.decode(tuple[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(tuple[1], StandardCharsets.UTF_8));
            }
        }
        return components[0];
    }

    public String getQuery(String key) {
        if(key == null) {
            return null;
        }
        return queries.get(key);
    }

}
