package com.example.demo.controller;


import com.example.demo.dto.EntryDTO;
import com.example.demo.entity.Member;
import com.example.demo.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class CalendarController {


    private final CalendarService calendarService;



    @GetMapping("/upload/{date}")
    public ResponseEntity<?> uploadEntry(
            @RequestParam("text") String text,
            @PathVariable("date") String dateStr, // 날짜를 문자열로 받음
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal Member member) {
        // 이미지 파일 제대로 받아왔는지
        log.info("Received file: {}", imageFile != null ? imageFile.getOriginalFilename() : "No file");

        try {
            // 받은 날짜 문자열을 LocalDate로 변환
            log.info("UPLOAD Date 실행 ");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate date = LocalDate.parse(dateStr,formatter);

            // EntryDTO 객체를 만들어서 저장
            EntryDTO dto = EntryDTO.builder()
                    .text(text)
                    .date(date)
                    .build();

            // 서비스에서 이미지 파일 처리 및 EntryDTO 저장
            EntryDTO savedEntry = calendarService.saveEntry(dto, member, imageFile);

            return ResponseEntity.ok(savedEntry);
        } catch (IOException e) {
            log.error("Error uploading entry: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save entry: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error parsing date: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format");
        }
    }




}
