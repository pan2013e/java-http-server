package HttpServer.protocol;

import HttpServer.Utils;
import HttpServer.servlet.DispatcherServlet;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final HttpRequest httpRequest;

    private HttpResponseType responseType;

    private String responseBody = "";

    private final Map<String, String> responseHeader = new LinkedHashMap<>();

    public HttpResponse(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        setResponse(HttpResponseType.OK);
        setHeader("Server", "JavaHttpServer");
        setHeader("Date", getServerTime());
        setHeader("Content-Type", "text/html; charset=UTF-8");
        setHeader("Connection", "close");
    }

    public void setResponse(HttpResponseType responseType) {
        this.responseType = responseType;
    }

    private void setBody(Object responseBody) {
        this.responseBody = responseBody.toString();
    }

    public void setHeader(String key, String value) {
        responseHeader.put(key, value);
    }

    public String getHeader(String key) {
        return responseHeader.get(key);
    }

    public void setContentType(String value) {
        setHeader("Content-Type", value);
    }

    private void doInvoke(Method m) throws InvocationTargetException,
            IllegalAccessException, UnsupportedOperationException {
        if(m == null) {
            throw new UnsupportedOperationException();
        }
        Class<?>[] pTypes = m.getParameterTypes();
        List<Object> args = new ArrayList<>();
        for(int i = 0;i < m.getParameterCount();i++) {
            if (HttpRequest.class.equals(pTypes[i])) {
                args.add(httpRequest);
            } else if (HttpResponse.class.equals(pTypes[i])) {
                args.add(this);
            } else if (String.class.equals(pTypes[i])) {
                String arg = httpRequest.getQuery(Utils.getQueryParameterName(m, i));
                args.add(arg);
            } else {
                throw new UnsupportedOperationException("Unsupported type");
            }
        }
        Object ret = m.invoke(null, args.toArray());
        if(ret instanceof String || !getHeader("Content-Type").startsWith("text/html")) {
            setBody(ret);
        } else {
            Gson gson = new Gson();
            setContentType("text/json; charset=UTF-8");
            setBody(gson.toJson(ret));
        }
    }

    public void processRequest() {
        try {
            Method m = DispatcherServlet.get(httpRequest.getResourceURI(), httpRequest.getMethod());
            doInvoke(m);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            setResponse(HttpResponseType.NOT_FOUND);
        }

    }

    @Override
    public String toString() {
        assert responseType != null;
        StringBuilder sb = new StringBuilder();
        int statusCode = responseType.getCode();
        String status = responseType.toString();
        sb.append(String.format("HTTP/1.1 %d %s\r\n", statusCode, status));
        for(var key : responseHeader.keySet()) {
            sb.append(String.format("%s: %s\r\n", key, responseHeader.get(key)));
        }
        sb.append("\r\n");
        if(responseType.equals(HttpResponseType.OK) || !responseBody.equals("")) {
            sb.append(responseBody);
        } else {
            sb.append(String.format("""
                <html>
                <head><title>%d %s</title></head>
                <body>
                    <center><h1>%d %s</h1></center>
                    <hr><center>Java HTTP Server</center>
                </body>
                </html>
                """,
                statusCode, status, statusCode, status));
        }
        return sb.toString();
    }

    // RFC 1123 (HTTP/1.1)
    private String getServerTime() {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(
                ZonedDateTime.now(ZoneOffset.UTC)
        );
    }

}
