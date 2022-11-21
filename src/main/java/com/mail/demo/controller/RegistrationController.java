package com.mail.demo.controller;



import com.mail.demo.entity.User;
import com.mail.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    @Autowired
    UserService userService;

    @GetMapping("/registration")
    public String getRegistration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute(name = "user") User user,
                               @ModelAttribute(name = "confirmPassword") String confirmPassword,
                               RedirectAttributes redirectAttributes){
        return userService.registerUser(user, confirmPassword, redirectAttributes);
    }
}
