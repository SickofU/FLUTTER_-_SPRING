package com.example.demo.repository;

import com.example.demo.entity.Friendship;
import com.example.demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    // 특정 사용자가 맺은 친구 관계 목록을 가져오는 메서드
    List<Friendship> findByUser(Member user);
}
