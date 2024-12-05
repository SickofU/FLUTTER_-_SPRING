package com.example.demo.dto;

import lombok.*;

import java.time.LocalDate;
// 캘린더와 앨범 탭은 같은 디티오를 쓰니까
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntryDTO {
    private Long id;
    private LocalDate date;
    private String imageUrl;
    private String text;

    // 생성자, getter, setter
}