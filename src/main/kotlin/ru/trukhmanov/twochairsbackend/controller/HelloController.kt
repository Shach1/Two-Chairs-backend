package ru.trukhmanov.twochairsbackend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.trukhmanov.twochairsbackend.util.CurrentUser

@RestController
class HelloController {

    @GetMapping("/hello")
    fun getHello(): String {
        return "Hello ${CurrentUser.id()}"
    }
}
