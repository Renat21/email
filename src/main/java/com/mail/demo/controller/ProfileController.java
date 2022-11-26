package com.mail.demo.controller;


import com.mail.demo.entity.User;
import com.mail.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
public class ProfileController {

    @Autowired
    UserService userService;

    @GetMapping("/profile")
    public String getProfile(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("user", userService.findUserByUsername(user.getUsername()));
        return "profile";
    }


    @PostMapping("/edit/profile")
    public String changeUserInfo(RedirectAttributes redirectAttributes,
                                 @RequestParam("username") String username,
                                 @RequestParam("name") String name,
                                 @RequestParam("surname") String surname, @AuthenticationPrincipal User user) {

        return userService.changeProfile(redirectAttributes, userService.findUserByUsername(user.getUsername()),
                username, name, surname);
    }

    @PostMapping("/edit/photo")
    public String changePhoto(RedirectAttributes redirectAttributes, @RequestParam("file") MultipartFile file,
                              @AuthenticationPrincipal User user) throws IOException {
        return userService.changePhoto(redirectAttributes, file, userService.findUserByUsername(user.getUsername()));
    }


    @PostMapping("/edit/password")
    public String changePassword(RedirectAttributes redirectAttributes,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("passwordConfirm") String passwordConfirm, @AuthenticationPrincipal User user) {
        return userService.changePassword(user, currentPassword, newPassword, passwordConfirm, redirectAttributes);
    }
}
