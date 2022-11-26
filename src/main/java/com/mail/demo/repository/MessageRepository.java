package com.mail.demo.repository;

import com.mail.demo.entity.Message;
import com.mail.demo.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {


    List<Message> findAllByUserTo(User userTo, Pageable pageable);

    Message findMessageById(Long id);
}