package com.mail.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MessagesController {
    @GetMapping("/messages")
    public String getMessages(){
        return "messages";
    }
}
