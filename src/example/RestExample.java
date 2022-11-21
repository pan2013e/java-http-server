package example;

import HttpServer.annotation.Controller;
import HttpServer.annotation.GetMapping;
import HttpServer.annotation.Query;
import lombok.Data;

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
