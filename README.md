# Java Http Server

## Dependencies

- Java 17
- Gson 2.10
- lombok 1.8.x

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
public class TestController {

    @GetMapping("/")
    public static Record index(@Query("name") String name,
                               @Query("sex") String sex) {
        Record record = new Record();
        record.setName(name);
        record.setSex(sex);
        return record;
    }

    @GetMapping("/test")
    public static String test() {
        return """
                <html>
                    <head>
                        <title>Test</title>
                    </head>
                    <body>
                        <h1>Hello world!</h1>
                    </body>
                </html>
                """;
    }

}
```