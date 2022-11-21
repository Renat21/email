package com.mail.demo.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Messages {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User userFrom;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User userTo;

    private String content;



    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="chat_images",
            joinColumns = {@JoinColumn(name="chat_message_id")},
            inverseJoinColumns = {@JoinColumn(name="image_id")}
    )
    private List<Image> images = new ArrayList<>();

    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime time;
}
