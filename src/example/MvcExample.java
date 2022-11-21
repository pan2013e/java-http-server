package example;

import HttpServer.annotation.Controller;
import HttpServer.annotation.GetMapping;
import HttpServer.annotation.Query;
import HttpServer.mvc.ModelAndView;
import lombok.NonNull;

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
