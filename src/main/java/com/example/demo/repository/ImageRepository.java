package com.example.demo.repository;

import com.example.demo.entity.Image;
import com.example.demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUser_Id(Long userId);  // 올바른 속성 이름 사용 (userId)

}