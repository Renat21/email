package com.mail.demo.controller;



import com.mail.demo.entity.User;
import com.mail.demo.service.FriendService;
import com.mail.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class RegistrationController {

    @Autowired
    UserService userService;

    @Autowired
    FriendService friendService;

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


    @GetMapping("/user/{username}/friend")
    public String sendInvite(@PathVariable Optional<String> username) {
        return friendService.sendInvite(username.get(), "");
    }

    @GetMapping("/user/{username}/friend/{result}")
    public String acceptInvite(@PathVariable Optional<String> username, @AuthenticationPrincipal User authenticatedUser, @PathVariable int result,
                               @RequestParam(value = "where", required = false) String where) {
        return friendService.resultInvite(username.get(), result, where);
    }

    @GetMapping("/user/{username}/unfriend")
    public String deleteFriend(@PathVariable Optional<String> username, @AuthenticationPrincipal User authenticatedUser,
                               @RequestParam(value = "where", required = false) String where) {

        return friendService.deleteFriend(username.get(), where);
    }

//    @RequestMapping(value = "/possibleFriends/{username}", method = RequestMethod.GET)
//    @ResponseBody
//    public List<Object> possibleFriends(@PathVariable String username, @AuthenticationPrincipal User authenticatedUser){
//
//        return userService.findPossibleAndMutualFriends(authenticatedUser);
//    }
//
//    @RequestMapping(value = "/addPossibleFriend/{userId}", method = RequestMethod.GET)
//    @ResponseBody
//    public void addPossibleFriend(@PathVariable Long userId){
//        friendService.addPossibleFriend(userService.findUserById(userId).getUsername());
//    }
}
