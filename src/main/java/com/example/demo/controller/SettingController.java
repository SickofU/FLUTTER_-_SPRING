package com.example.demo.controller;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.MemberProfileInfoDto;
import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.dto.SettingResponseDTO;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.SettingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/setting")
public class SettingController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;




    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal Member member) {
        if (member == null) {
            log.info("delete account failed because member doesnt exists");
            return ResponseEntity.badRequest().body("User not authenticated.");
        }

        memberRepository.deleteById(member.getId());
        log.info("successfully deleted");
        return ResponseEntity.ok("Account deleted successfully.");
    }


@PatchMapping("/profileinfo")
public ResponseEntity<String> updateProfileInfo(
        @AuthenticationPrincipal Member member,
        @RequestBody ProfileUpdateRequest request) {
    if (member == null) {
        log.info("update profileinfo failed because member doesnt exists");
        return ResponseEntity.badRequest().body("NO USER");
          }

    // 변경할 값이 있으면 업데이트
    try {
        member.setUsername(request.getName());
        member.setBirthdate(request.getBirthdate());
        member.setFriendName(request.getPartnerName());
        member.setFriendBirthdate(request.getPartnerBirthdate());
        // 업데이트 저장
        memberRepository.save(member);
        return ResponseEntity.ok("Success");
    }
    catch (Exception e) {
        log.info("update profileinfo failed");
    }
    return ResponseEntity.badRequest().body("NO USER");
}



    @GetMapping("/profileinfo")
    public ResponseEntity<MemberProfileInfoDto> getProfileInfo(@AuthenticationPrincipal Member member) {
        log.info("getProfileInfo");
        if (member == null) {
            log.info("getProfileInfo failed because member doesnt exists");
            return ResponseEntity.badRequest().body(null);
        }

        MemberProfileInfoDto profileInfo = new MemberProfileInfoDto(
                member.getId(),
                member.getEmail(),
                member.getUsername()
        );
    log.info("return Profile INFO");
        return ResponseEntity.ok(profileInfo);
    }





}
