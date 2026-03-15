package ru.trukhmanov.twochairsbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class HelloController{

    @GetMapping("/hello")
    public String getHello(){
        return "Hello Spring";
    }
}
