# Java Http Server

## Dependencies

- Java 17
- Gson
- lombok
- freemarker

> Be sure to enable annotation processors 

## Usage

### Main class

```java
@HttpApplication
public class Main {

    public static void main(String[] args) {
        HttpServerStarter.start(Main.class);
    }

}
```

The server will start listening on `0.0.0.0:8080` by default.

### Controller class

```java
@Controller("/")
public class MvcExample {

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index.html");
        return mav;
    }

    @GetMapping("/test")
    public ModelAndView test(@Query("user") @NonNull String user) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("test");
        mav.addObject("user", user);
        return mav;
    }

}
```

```java
@Controller("/rest")
public class RestExample {

    @Data
    public static class Record {
        private String name;
        private String sex;
    }

    @GetMapping("/")
    public Record test(@Query("name") String name,
                       @Query("sex") String sex) {
        Record record = new Record();
        record.setName(name);
        record.setSex(sex);
        return record;
    }

}
```

## TODO

- URGENT: host static resources (*.js, *.css, *.jpg, etc.)
- Make setting resources directory more flexible
- Support more HTTP methods
- Support request interceptors (i.e., blacklists & whitelists)