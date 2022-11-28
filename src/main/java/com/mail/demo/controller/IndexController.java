package com.mail.demo.controller;

import com.mail.demo.entity.User;
import com.mail.demo.service.MessageService;
import com.mail.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @GetMapping("/")
    public String mainPage(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("user", userService.findUserByUsername(user.getUsername()));
        return "index";
    }
}
