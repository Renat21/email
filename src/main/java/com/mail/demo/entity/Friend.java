package com.mail.demo.entity;

import com.mail.demo.enumer.InviteStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Class Friend - класс сущности друзей пользователя
 **/
@NoArgsConstructor
@Entity(name = "friend")
@Table(name = "friends")
@Getter
@Setter

/**
 * Class Friend - класс друзей пользователя
 **/
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    @JoinColumn(name = "first_user_id", referencedColumnName = "id")
    private User firstUser;

    @OneToOne(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    @JoinColumn(name = "second_user_id", referencedColumnName = "id")
    private User secondUser;

    @Enumerated(EnumType.STRING)
    private InviteStatus inviteStatus;

    public Friend(User firstUser, User secondUser, InviteStatus inviteStatus) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.inviteStatus = inviteStatus;
    }
}
