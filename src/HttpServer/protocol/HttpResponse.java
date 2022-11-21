package HttpServer.protocol;

import HttpServer.Utils;
import HttpServer.annotation.JSONSerializable;
import HttpServer.mvc.ModelAndView;
import HttpServer.mvc.TemplateFactory;
import HttpServer.servlet.ControllerFactory;
import HttpServer.servlet.DispatcherServlet;
import com.google.gson.Gson;
import freemarker.template.Template;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final HttpRequest httpRequest;
    private final BufferedOutputStream writer;

    private HttpResponseType responseType;

    private Object responseBody = "";

    private final Map<String, String> responseHeader = new LinkedHashMap<>();

    public HttpResponse(HttpRequest httpRequest, BufferedOutputStream writer) {
        this.httpRequest = httpRequest;
        this.writer = writer;
        setResponse(HttpResponseType.OK);
        setHeader("Server", Utils.SERVER_NAME);
        setHeader("Date", Utils.getServerTime());
        setHeader("Content-Type", "text/html; charset=UTF-8");
        setHeader("Connection", "close");
    }

    public void setResponse(HttpResponseType responseType) {
        this.responseType = responseType;
    }

    private void setBody(Object responseBody) {
        this.responseBody = responseBody;
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

    public void setContentLength(int value) {
        setHeader("Content-Length", String.valueOf(value));
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
        Object ret;
        if(Modifier.isStatic(m.getModifiers())) {
            ret = m.invoke(null, args.toArray());
        } else {
            ret = m.invoke(ControllerFactory.getInstance().getController(m.getDeclaringClass()), args.toArray());
        }
        if(ret == null) {
            return;
        }
        if(ret instanceof ModelAndView _ret) {
            try {
                Template template = TemplateFactory.getInstance().getTemplate(_ret.getViewName());
                StringWriter sw = new StringWriter();
                BufferedWriter bw = new BufferedWriter(sw);
                Method getMap = ModelAndView.class.getDeclaredMethod("getMap");
                getMap.setAccessible(true);
                template.process(getMap.invoke(_ret), bw);
                setBody(sw);
            } catch (Exception e) {
                e.printStackTrace();
                throw new UnsupportedOperationException(e.getMessage());
            }
        } else if(ret instanceof String || ret instanceof byte[]) {
            setBody(ret);
        } else if(ret.getClass().isAnnotationPresent(JSONSerializable.class)) {
            setBody(ret);
            setContentType("text/json; charset=UTF-8");
        } else {
            Gson gson = new Gson();
            setContentType("text/json; charset=UTF-8");
            setBody(gson.toJson(ret));
        }
    }

    public void processRequest() {
        try {
            Method m = DispatcherServlet.get(httpRequest.getResourceURI(), httpRequest.getMethod());
            if(m == null) {
                throw new NullPointerException();
            }
            m.setAccessible(true);
            doInvoke(m);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException | NullPointerException e) {
            setResponse(HttpResponseType.NOT_FOUND);
        }

    }

    public void send() throws IOException {
        int statusCode = responseType.getCode();
        String status = responseType.toString();
        writer.write(String.format("HTTP/1.1 %d %s\r\n", statusCode, status).getBytes(StandardCharsets.UTF_8));
        for(var key : responseHeader.keySet()) {
            writer.write(String.format("%s: %s\r\n", key, responseHeader.get(key)).getBytes(StandardCharsets.UTF_8));
        }
        writer.write("\r\n".getBytes(StandardCharsets.UTF_8));
        if(responseType.equals(HttpResponseType.OK) || !responseBody.toString().equals("")) {
            if(responseBody instanceof byte[] _bytes) {
                writer.write(_bytes);
            } else {
                writer.write(responseBody.toString().getBytes(StandardCharsets.UTF_8));
            }
        } else {
            writer.write(String.format("""
                <html>
                <head><title>%d %s</title></head>
                <body>
                    <center><h1>%d %s</h1></center>
                    <hr><center>Java HTTP Server</center>
                </body>
                </html>
                """,
                    statusCode, status, statusCode, status).getBytes(StandardCharsets.UTF_8));
        }
        writer.flush();
    }

}
