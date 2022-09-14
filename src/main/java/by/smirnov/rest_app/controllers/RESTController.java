package by.smirnov.rest_app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // = @Controller + @ResponseBody надо каждым методом
@RequestMapping("/api")
public class RESTController {

    @GetMapping("/sayHello")
    public String sayHello(){
        return "Up the Irons!";
    }
}
