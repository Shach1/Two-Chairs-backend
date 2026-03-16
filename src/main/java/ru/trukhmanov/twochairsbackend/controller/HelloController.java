package ru.trukhmanov.twochairsbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trukhmanov.twochairsbackend.util.CurrentUser;

@RestController()
public class HelloController{

    @GetMapping("/hello")
    public String getHello(){
        return "Hello " + CurrentUser.id();
    }
}
