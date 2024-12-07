package com.example.demo.service;

import com.example.demo.converter.EntryConverter;
import com.example.demo.dto.EntryDTO;
import com.example.demo.entity.Album;
import com.example.demo.entity.Calendar;
import com.example.demo.entity.Member;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.CalendarRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlbumService {


    private final CalendarRepository calendarRepository;
    private final EntryConverter entryConverter;


    // 특정 날짜의 앨범 데이터 조회 (자신과 친구의 데이터 포함)
    public List<EntryDTO> getAlbumEntries(Member member, LocalDate date) {
        // 친구 리스트 가져오기

        Member friend = member.getFriend();

        // 친구가 없는 경우 자기 자신만 조회
        List<Calendar> entries;
        if (friend == null) {
            // 친구가 없는 경우
            log.info("No Friend");
            entries = calendarRepository.findByMemberAndDate(member, date);
        } else {
            // 친구가 있는 경우 자신과 친구 데이터 모두 조회
            log.info("Friend");
            entries = calendarRepository.findByMemberInAndDate(List.of(member, friend), date);
        }

        return entries.stream().map(entryConverter::toDto).collect(Collectors.toList());
    }
}
