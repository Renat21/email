package com.mail.demo.repository;

import com.mail.demo.entity.Message;
import com.mail.demo.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {


    @Query("select m from Message m where m.userTo = ?1 and not (m.messageDeleted = 1)")
    List<Message> findAllByUserTo(User userTo, Pageable pageable);

    @Query("select m from Message m where m.userTo = ?1 and not (m.messageDeleted = 1) and CONCAT(m.content) LIKE CONCAT('%', (?2),'%')")
    List<Message> findAllByUserToWithSearch(User userTo, String searchLine, Pageable pageable);

    @Query("select count(m) from Message m where m.userTo = ?1 and not (m.messageDeleted = 1)")
    Long findCountByUserTo(User userTo);

    @Query("select count(m) from Message m where m.userTo = ?1 and not (m.messageDeleted = 1) and CONCAT(m.content) LIKE CONCAT('%', (?2),'%')")
    Long findCountByUserToWithSearch(User userTo, String searchLine);



    @Query("select m from Message m where m.userFrom = ?1 and not (m.messageDeleted = 0)")
    List<Message> findAllByUserFrom(User userFrom, Pageable pageable);

    @Query("select m from Message m where m.userFrom = ?1 and not (m.messageDeleted = 0) and CONCAT(m.content) LIKE CONCAT('%', (?2),'%')")
    List<Message> findAllByUserFromWithSearch(User userTo, String searchLine, Pageable pageable);


    @Query("select count(m) from Message m where m.userFrom = ?1 and not (m.messageDeleted = 0)")
    Long findCountByUserFrom(User userFrom);

    @Query("select count(m) from Message m where m.userFrom = ?1 and not (m.messageDeleted = 0) and CONCAT(m.content) LIKE CONCAT('%', (?2),'%')")
    Long findCountByUserFromWithSearch(User userFrom, String searchLine);

    Message findMessageById(Long id);
}