package com.mail.demo.controller;


import com.mail.demo.entity.Message;
import com.mail.demo.entity.User;
import com.mail.demo.enumer.MessageType;
import com.mail.demo.service.MessageService;
import com.mail.demo.service.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;


@Controller
public class MessagesController {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;


    @GetMapping("/messages")
    public String getMessages(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("user", userService.findUserByUsername(user.getUsername()));
        return "messages";
    }

    @PostMapping("/sendMessage")
    public String registration(@ModelAttribute(name = "message") Message message,
                               @ModelAttribute(name = "userToEmail") String email,
                               @AuthenticationPrincipal User userFrom){
        messageService.newMessage(message, userService.findUserByEmail(email), userFrom);
        return "redirect:/";
    }

    @MessageMapping("/message.send/{username}")
    @SendTo("/topic/messageMail/{username}")
    public Message addPost(@Payload final Message message) {
        messageService.saveMessage(message);
        return message;
    }

    @PostMapping("/messages/{page}/{searchLine}")
    @ResponseBody
    public List<Message> showMessages(@AuthenticationPrincipal User user, @PathVariable int page, @PathVariable String searchLine)
    {
        return messageService.showLastMessages(user,page, searchLine);
    }

    @PostMapping("/messagesSend/{page}/{searchLine}")
    @ResponseBody
    public List<Message> showSendMessages(@AuthenticationPrincipal User user, @PathVariable int page, @PathVariable String searchLine)
    {
        return messageService.showLastSendMessages(user,page, searchLine);
    }

    @PostMapping("/readMessage/{messageId}")
    @ResponseBody
    public void readMessage(@AuthenticationPrincipal User user, @PathVariable Long messageId){
        messageService.readMessage(user, messageId);
    }

    @PostMapping("/setStarMessage/{messageId}")
    @ResponseBody
    public void setStarMessage(@AuthenticationPrincipal User user, @PathVariable Long messageId){
        messageService.setStarMessage(user, messageId);
    }

    @PostMapping("/getMessagesReceived/{searchLine}")
    @ResponseBody
    public Long getMessageReceived(@AuthenticationPrincipal User user, @PathVariable String searchLine){
        return messageService.getMessagesReceived(user, searchLine);
    }

    @PostMapping("/getMessagesSend/{searchLine}")
    @ResponseBody
    public Long getMessageSent(@AuthenticationPrincipal User user, @PathVariable String searchLine){
        return messageService.getMessagesSent(user, searchLine);
    }

    @PostMapping("/deleteReceivedMessages")
    @ResponseBody
    public String deleteReceivedMessage(@AuthenticationPrincipal User user, @RequestBody String body){
        messageService.deleteReceivedMessagesMessages(user, new JSONObject(body).getJSONArray("messages"));
        return body;
    }

    @PostMapping("/deleteSendMessages")
    @ResponseBody
    public String deleteSendMessage(@AuthenticationPrincipal User user, @RequestBody String body){
        messageService.deleteSendMessagesMessages(user, new JSONObject(body).getJSONArray("messages"));
        return body;
    }
}
