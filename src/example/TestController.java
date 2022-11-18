package example;

import HttpServer.annotation.Controller;
import HttpServer.annotation.GetMapping;
import HttpServer.annotation.Query;

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
