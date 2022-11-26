package com.mail.demo.repository;

import com.mail.demo.entity.Friend;
import com.mail.demo.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {
    boolean existsByFirstUserAndSecondUser(User firstUser, User secondUser);

    Friend findFriendByFirstUserAndSecondUser(User firstUser, User secondUser);

    void deleteFriendByFirstUserAndSecondUser(User firstUser, User secondUser);

    List<Friend> findFriendsByFirstUser(User firstUser);


    List<Friend> findFriendsBySecondUser(User secondUser);


    @Query("select f from friend f where (f.firstUser = ?1 or f.secondUser = ?1) and f.inviteStatus ='ACCEPTED'")
    List<Friend> findFriendByFirstUserOrSecondUser(User first, Pageable pageable);


}
