package HttpServer.mvc;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;

public class TemplateFactory {

    private static final TemplateFactory tcfg = new TemplateFactory();

    private final Configuration cfg;

    public static TemplateFactory getInstance() {
        return tcfg;
    }

    private TemplateFactory() {
        cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    }

    public void setTemplateDir(String dir) throws IOException {
        cfg.setDirectoryForTemplateLoading(new File(dir));
    }

    public Template getTemplate(String name) {
        if(name == null) {
            return null;
        }
        try {
            if(name.matches(".*\\..*")) {
                return cfg.getTemplate(name);
            } else {
                return cfg.getTemplate(name + ".ftl");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
