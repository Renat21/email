package com.mail.demo.service;


import com.mail.demo.entity.Message;
import com.mail.demo.entity.User;
import com.mail.demo.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final static int MESSAGE_PAGE_SIZE = 15;
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserService userService;


    public void save(Message message){
        messageRepository.save(message);
    }

    public void newMessage(Message message, User userTo, User userFrom){
        message.setUserTo(userTo);
        message.setUserFrom(userFrom);
        save(message);
    }

    public void saveMessage(Message message){
        message.setUserTo(userService.findUserByEmail(message.getUserTo().getEmail()));
        messageRepository.save(message);
    }

    public List<Message> showLastMessages(User user, int page) {
        Pageable pageable = PageRequest.of(page, MESSAGE_PAGE_SIZE);
        return messageRepository.findAllByUserTo(user, pageable);
    }

    public Message getMessageById(Long messageId){
        return messageRepository.findMessageById(messageId);
    }
}

