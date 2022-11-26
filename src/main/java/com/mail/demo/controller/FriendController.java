package com.mail.demo.controller;


import com.mail.demo.entity.User;
import com.mail.demo.service.FriendService;
import com.mail.demo.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class FriendController {

    @Autowired
    UserService userService;

    @Autowired
    FriendService friendService;



    @GetMapping("/friends")
    public String getFriendList(Model model, @ModelAttribute("searchLine") String searchLine, @AuthenticationPrincipal User user) {

        String username = user.getUsername();
        model.addAttribute("searchLine", searchLine);
        model.addAttribute("friendService", friendService);
        model.addAttribute("user", userService.findUserByUsername(username));
        model.addAttribute("friends", friendService.getAcceptedFriends(username));
        model.addAttribute("isFriend", friendService.isFriends(username));

        return "friends";
    }

    @RequestMapping(value = "/friends/reloadFriendList/{page}", method = RequestMethod.POST)
    @ResponseBody
    public List<Object> processReloadData(@RequestBody String body, @PathVariable Optional<Integer> page,
                                          @AuthenticationPrincipal User user) {


        String username = user.getUsername();

        JSONObject request = new JSONObject(body);
        String searchLine = friendService.clearSearchLine(request.getString("searchLine")).
                replaceAll("[\s]{2,}", " ").trim();

        return friendService.findFriendsAndStrangers(username, searchLine, page.get());
    }

    @RequestMapping(value = "/friends/reloadSuggestionList/{page}", method = RequestMethod.POST)
    @ResponseBody
    public List<User> processReloadInviteData(@RequestBody String body, @PathVariable Optional<Integer> page,
                                              @AuthenticationPrincipal User user) {

        String username = user.getUsername();
        JSONObject request = new JSONObject(body);
        String searchLine = friendService.clearSearchLine(request.getString("searchLine")).
                replaceAll("[\s]{2,}", " ").trim();

        return friendService.findSuggestions(username, searchLine, page.get());
    }
}
