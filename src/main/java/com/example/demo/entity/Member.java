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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "authority")
    private List<String> authorities = new ArrayList<>();


    @Column(nullable = true)
    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "friend_id")
    private Member friend; // 친구

    // 기본 생성자
    public Member() {}

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getFriendName() { return friendName; }
    public Member getFriend() { return friend; }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setFriend(Member friend) { this.friend = friend; }
    public void setFriendName(String friendName) { this.friendName = friendName; }
public Long getFriendId(){
        return friend.getId();
}
    @Builder
    public Member(String username, String email, String password,List<String> authorities) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities != null ? authorities : List.of("USER");  // 기본 권한 설정

    }

}
