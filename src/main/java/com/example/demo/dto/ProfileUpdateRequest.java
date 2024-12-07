package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    private String name;             // 변경할 이름
    private String birthdate;        // 변경할 생일
    private String partnerName;      // 변경할 파트너 이름
    private String partnerBirthdate; // 변경할 파트너 생일
}
