package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member user;

    // 기본 생성자
    public Image() {}

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getFilename() { return filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Member getUser() { return user; }

    public void setUser(Member user) { this.user = user; }
}
