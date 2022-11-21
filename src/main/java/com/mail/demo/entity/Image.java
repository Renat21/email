package com.mail.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "originalFileName")
    private String originalFileName;

    @Column(name = "size")
    private Long size;

    @Column(name = "contentType")
    private String contentType;

    @Lob
    @JsonIgnore
    private byte[] bytes;


    public Image(String name, String originalFileName, Long size, String contentType, byte[] bytes) {
        this.name = name;
        this.originalFileName = originalFileName;
        this.size = size;
        this.contentType = contentType;
        this.bytes = bytes;
    }

}
