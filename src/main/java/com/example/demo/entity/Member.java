package com.example.demo.entity;

import com.example.demo.service.UserDetailServiceImp;
import jakarta.persistence.*;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Member implements UserDetails {
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(authority -> (GrantedAuthority) () -> authority)
                .collect(Collectors.toList());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String email;


    private String username;


    private String password;


    private String friendName; // 친구 이름

    private String Birthdate;

    private String friendBirthdate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "authority")
    private List<String> authorities = new ArrayList<>();


    @Column(nullable = true)
    private String refreshToken;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "friend_id")
    private Member friend; // 친구

    // 기본 생성자
    public Member() {}

    // Getters and Setters
    public Long getId() { return id; }

public void setBirthdate(String birthdate) {
        this.Birthdate = birthdate;
}
public String getBirthdate() {
        return Birthdate;
}
public void setFriendBirthdate(String friendBirthdate) {
        this.friendBirthdate = friendBirthdate;
}
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }

    public void setUsername(String username) {  }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getFriendName() { return friendName; }
    public Member getFriend() { return friend; }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setFriend(Member friend) {
        this.friend = friend;
        if (friend != null && friend.getFriend() != this) {
            friend.setFriend(this); // 양방향 관계 설정
        }
    }
    public void setFriendName(String friendName) { this.friendName = friendName; }
public Long getFriendId(){
        return friend.getId();
}
    public void setFriendId(Long friendId) {
        if (friend != null && friend.getId().equals(friendId)) {
            return; // 이미 설정된 경우 무시
        }

        if (friend == null) {
            this.friend = new Member(); // 새로운 친구 객체 생성
        }

        this.friend.setId(friendId); // 친구 ID 설정
    }
    @Builder
    public Member(String username, String email, String password,List<String> authorities) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities != null ? authorities : List.of("USER");  // 기본 권한 설정

    }

}
