package HttpServer.servlet;

import HttpServer.Utils;
import HttpServer.protocol.HttpMethod;
import HttpServer.protocol.HttpRequest;
import HttpServer.protocol.HttpResponse;
import HttpServer.protocol.HttpResponseType;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticResourceHandler {

    public static StaticResourceHandler getInstance() {
        return INSTANCE;
    }

    private static final StaticResourceHandler INSTANCE = new StaticResourceHandler();

    private StaticResourceHandler() {}

    @Getter
    @Setter
    private String uriBase = "/";

    @Getter
    @Setter
    private String fsBase;

    public void addRule(String wildcard) {
        try {
            DispatcherServlet.add(Utils.uriConcat(uriBase, wildcard), HttpMethod.GET,
                    this.getClass().getMethod("getStatic", HttpRequest.class, HttpResponse.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Object getStatic(HttpRequest req, HttpResponse res) {
        String requestURI = req.getResourceURI();
        Path p = Utils.fsConcat(getInstance().getFsBase(), requestURI.substring(1));
        File f = new File(p.toAbsolutePath().toString());
        if(f.isFile()) {
            try {
                String mimeType = Files.probeContentType(p);
                FileInputStream fis = new FileInputStream(f);
                byte[] bytes = fis.readAllBytes();
                fis.close();
                res.setContentType(mimeType);
                res.setContentLength(bytes.length);
                if(mimeType.startsWith("text")) {
                    return new String(bytes, StandardCharsets.UTF_8);
                } else {
                    return bytes;
                }
            } catch (IOException e) {
                res.setResponse(HttpResponseType.INTERNAL_SERVER_ERROR);
                return null;
            }
        } else {
            res.setResponse(HttpResponseType.NOT_FOUND);
            return null;
        }
    }


}
