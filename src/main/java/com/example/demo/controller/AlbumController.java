package com.example.demo.controller;

import com.example.demo.dto.EntryDTO;
import com.example.demo.entity.Member;
import com.example.demo.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/album")
@RequiredArgsConstructor
public class AlbumController {

    
    private final AlbumService albumService;


    @GetMapping("/{date}")
    public ResponseEntity<List<EntryDTO>> getAlbumEntries(@PathVariable String date, @AuthenticationPrincipal Member member) {
        LocalDate localDate = LocalDate.parse(date);
        List<EntryDTO> entries = albumService.getAlbumEntries(member, localDate);
        return ResponseEntity.ok(entries);
    }
}