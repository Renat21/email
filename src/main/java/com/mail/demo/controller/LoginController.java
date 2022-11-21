package com.mail.demo.controller;


import com.mail.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }
}
