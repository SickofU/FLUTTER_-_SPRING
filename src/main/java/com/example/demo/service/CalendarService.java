package com.example.demo.service;
import com.example.demo.converter.EntryConverter;
import com.example.demo.dto.EntryDTO;
import com.example.demo.entity.Calendar;
import com.example.demo.entity.Member;
import com.example.demo.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;

    private final EntryConverter entryConverter;
    private final S3Uploader s3Uploader;


    // 캘린더 데이터 저장
    public EntryDTO saveEntry(EntryDTO dto, Member member, MultipartFile imageFile) throws IOException {
        Calendar entry = entryConverter.toCalendar(dto, member);

        // 이미지 업로드 및 URL 저장
        if (imageFile != null && !imageFile.isEmpty()) {
            //upload 왜 안돼지
// 친구데 대해서 저장하는거 기능 넣기

            String imageUrl = s3Uploader.upload(imageFile, member, "calendar");
            entry.setImageUrl(imageUrl); // Calendar 엔터티에 이미지 URL 저장
        }

        calendarRepository.save(entry);
        return entryConverter.toDto(entry);
    }

    // 특정 날짜 데이터 조회
    public List<EntryDTO> getEntriesByDate(Member member,  LocalDate date) {
        List<Calendar> entries = calendarRepository.findByMemberAndDate(member, date);
        return entries.stream().map(entryConverter::toDto).collect(Collectors.toList());
    }

}
