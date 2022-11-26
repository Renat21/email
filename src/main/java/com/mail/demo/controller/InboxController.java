package com.mail.demo.controller;


import com.mail.demo.entity.User;
import com.mail.demo.service.MessageService;
import com.mail.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class InboxController {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @GetMapping("/inbox/{messageId}")
    public String mainPage(Model model, @AuthenticationPrincipal User user, @PathVariable Long messageId){
        model.addAttribute("user", userService.findUserByUsername(user.getUsername()));
        model.addAttribute("message", messageService.getMessageById(messageId));
        return "inbox";
    }
}
