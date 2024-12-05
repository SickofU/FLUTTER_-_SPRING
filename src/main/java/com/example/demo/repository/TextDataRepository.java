package com.example.demo.repository;

import com.example.demo.entity.TextData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TextDataRepository extends JpaRepository<TextData, Long> {
    List<TextData> findByUser_Id(Long userId);  // 올바른 속성 이름 사용 (userId)
}

