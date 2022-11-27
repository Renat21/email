package com.mail.demo.repository;

import com.mail.demo.entity.Message;
import com.mail.demo.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {


    @Query("select m from Message m where m.userTo = ?1")
    List<Message> findAllByUserTo(User userTo, Pageable pageable);

    List<Message> findAllByUserFrom(User userFrom, Pageable pageable);

    Message findMessageById(Long id);
}