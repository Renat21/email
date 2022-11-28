package com.mail.demo.service;


import com.mail.demo.entity.Message;
import com.mail.demo.entity.User;
import com.mail.demo.enumer.MessageDeleted;
import com.mail.demo.repository.MessageRepository;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public List<Message> showLastMessages(User user, int page, String searchLine) {
        Pageable pageable = PageRequest.of(page, MESSAGE_PAGE_SIZE, Sort.by("time").descending());
        if (Objects.equals(searchLine, "default")) {
            return messageRepository.findAllByUserTo(user, pageable);
        }else {
            return messageRepository.findAllByUserToWithSearch(user, searchLine, pageable);
        }
    }

    public Long getMessagesReceived(User user, String searchLine){
        if (Objects.equals(searchLine, "default")) {
            return messageRepository.findCountByUserTo(user);
        }else {
            return messageRepository.findCountByUserToWithSearch(user, searchLine);
        }
    }

    public List<Message> showLastSendMessages(User user, int page, String searchLine) {
        Pageable pageable = PageRequest.of(page, MESSAGE_PAGE_SIZE, Sort.by("time").descending());
        if (Objects.equals(searchLine, "default")) {
            return messageRepository.findAllByUserFrom(user, pageable);
        }else {
            return messageRepository.findAllByUserFromWithSearch(user, searchLine, pageable);
        }
    }

    public Long getMessagesSent(User user, String searchLine){
        if (Objects.equals(searchLine, "default")) {
            return messageRepository.findCountByUserFrom(user);
        }else {
            return messageRepository.findCountByUserFromWithSearch(user, searchLine);
        }
    }

    public void readMessage(User user, Long messageId){
        Message message = messageRepository.findMessageById(messageId);
        if (Objects.equals(message.getUserTo().getId(), user.getId()) && !message.isMessageRead()){
            message.setMessageRead(true);
            save(message);
        }
    }

    public void setStarMessage(User user, Long messageId){
        Message message = messageRepository.findMessageById(messageId);
        if (Objects.equals(message.getUserTo().getId(), user.getId()) && !message.isStar()){
            message.setStar(true);
            save(message);
        }else if (Objects.equals(message.getUserTo().getId(), user.getId()) && message.isStar()){
            message.setStar(false);
            save(message);
        }
    }

    @Transactional
    public void deleteMessageFromReceivedMessages(User user, Message message){
        if (Objects.equals(message.getMessageDeleted(), MessageDeleted.NOT_DELETED)){
            message.setMessageDeleted(MessageDeleted.USER_TO);
            save(message);
        }else if (Objects.equals(message.getMessageDeleted(), MessageDeleted.USER_FROM))
        {
            message.setImages(new ArrayList<>());
            save(message);
            messageRepository.delete(message);
        }
    }
    public void deleteReceivedMessagesMessages(User user, JSONArray messages){
        for (Object message : messages) {
            deleteMessageFromReceivedMessages(user, messageRepository.findMessageById(Long.parseLong(message.toString())));
        }
    }

    @Transactional
    public void deleteMessageFromSendMessages(User user, Message message){
        if (Objects.equals(message.getMessageDeleted(), MessageDeleted.NOT_DELETED)){
            message.setMessageDeleted(MessageDeleted.USER_FROM);
            save(message);
        }else if (Objects.equals(message.getMessageDeleted(), MessageDeleted.USER_TO))
        {
            message.setImages(new ArrayList<>());
            save(message);
            messageRepository.delete(message);
        }
    }
    public void deleteSendMessagesMessages(User user, JSONArray messages){
        for (Object message : messages) {
            deleteMessageFromSendMessages(user, messageRepository.findMessageById(Long.parseLong(message.toString())));
        }
    }

    public Message getMessageById(Long messageId){
        return messageRepository.findMessageById(messageId);
    }

}

