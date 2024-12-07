package com.example.demo.controller;

import com.example.demo.dto.EntryDTO;
import com.example.demo.entity.Member;
import com.example.demo.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/album")
@RequiredArgsConstructor
public class AlbumController {

    
    private final AlbumService albumService;


    @GetMapping("/{date}")
    public ResponseEntity<List<EntryDTO>> getAlbumEntries(@PathVariable String date, @AuthenticationPrincipal Member member) {
        log.info("Generate Album");
        if(member == null){
            throw new IllegalStateException("Member is null in AuthenticationPrincipal");
        }
        // yyyyMMdd 형식을 처리하기 위한 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(date, formatter); // 명시적으로 파싱
        log.info("타임 맞추기 형식");
        List<EntryDTO> entries = albumService.getAlbumEntries(member, localDate);
        log.info("Return Info");
        return ResponseEntity.ok(entries);
    }
}