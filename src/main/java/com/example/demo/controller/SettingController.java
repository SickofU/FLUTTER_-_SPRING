//package com.example.demo.controller;
//
//import com.example.demo.config.JwtTokenProvider;
//import com.example.demo.dto.SettingResponseDTO;
//import com.example.demo.service.SettingService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/setting")
//public class SettingController {
//    private final SettingService settingService;
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @PatchMapping("/birthdate")
//    public ResponseEntity<SettingResponseDTO> updateBirthdate(){
//
//
//    }
//    @PatchMapping("/password")
//    public ResponseEntity<SettingResponseDTO> updatePassword(@RequestParam String password){
//
//    }
//    @GetMapping("/delete-account")
//    public void deleteAccount() {
//
//    }
//    @PatchMapping("/partnername")
//    public ResponseEntity<SettingResponseDTO> updatePartnerName(@RequestParam String partnerName){
//
//    }
//    @PatchMapping("/partnerbirthdate")
//    public ResponseEntity<SettingResponseDTO> updatePartnerBirthdate(@RequestParam String partnerBirthdate){
//
//    }
//    @GetMapping("/profileinfo")
//    public ResponseEntity<SettingResponseDTO> updateProfileInfo(@RequestParam String profileInfo){
//
//    }
//
//
//
//
//
//}
