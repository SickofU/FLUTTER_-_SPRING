package com.example.demo.entity;

import jakarta.persistence.*;
import com.example.demo.dto.EntryDTO;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; // 업로드된 날짜
    private String imageUrl; // 이미지 URL
    private String text; // 텍스트 데이터

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member; // 사용자와 매핑



    // 수정 메서드
    public void update(String newImageUrl, String newText) {
        this.imageUrl = newImageUrl;
        this.text = newText;
    }

    // 소유권 확인 메서드
    public boolean isOwnedBy(Member member) {
        return this.member.equals(member);
    }

    // 날짜 검증 메서드
    public boolean isDateValid(LocalDate date) {
        return !date.isAfter(LocalDate.now()); // 미래 날짜인지 확인
    }
}
