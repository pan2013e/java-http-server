package HttpServer.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PackageScanner {

    private static final ClassLoader loader = ClassLoader.getSystemClassLoader();

    private static final Logger scannerLogger = Logger.getLogger(PackageScanner.class.getName());

    private final List<Class<?>> classes = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();

    private final String packageName;

    private Class<? extends Annotation> scannedAnnot;

    public PackageScanner(Class<?> baseClass,
                          Class<? extends Annotation> annotClass) throws IOException {
        assert baseClass != null && annotClass != null;
        scannerLogger.setLevel(Level.INFO);
        packageName = baseClass.getPackageName();
        scannedAnnot = annotClass;
        scan();
    }

    public void setAnnotClass(Class<? extends Annotation> annotClass) {
        scannedAnnot = annotClass;
    }

    public Class<? extends Annotation> getAnnotClass() {
        return scannedAnnot;
    }

    public List<Class<?>> getClasses() {
        return classes.stream().filter(cls -> cls.isAnnotationPresent(scannedAnnot)).toList();
    }

    public String getPackageName() {
        return packageName;
    }

    private void scan() throws IOException {
        String basePath = packageName.replace(".", File.separator);
        List<String> absPaths = new ArrayList<>();
        var urls = loader.getResources(basePath);
        while(urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if(url != null) {
                if("file".equals(url.getProtocol())) {
                    absPaths.add(URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8));
                }
            }
        }
        scanDir(absPaths);
        try {
            for(String className: classNames) {
                if(packageName.equals("")) {
                    classes.add(Class.forName(className));
                } else {
                    classes.add(Class.forName(packageName + "." + className));
                }
            }
        } catch (ClassNotFoundException e) {
            scannerLogger.warning(e.getMessage());
            System.exit(1);
        }
    }

    private void scanDir(String path) {
        File f = new File(path);
        assert f.isDirectory();
        for(File _f : Objects.requireNonNull(f.listFiles())) {
            if(_f.isDirectory()) {
                scanDir(_f.getPath());
            } else {
                String fileName = _f.getName();
                if(fileName.endsWith(".class")) {
                    classNames.add(fileName.substring(0, fileName.lastIndexOf('.')));
                }
            }
        }
    }

    private void scanDir(List<String> paths) {
        for(String path: paths) {
            scanDir(path);
        }
    }

}
