package com.example.demo.converter;

import com.example.demo.dto.EntryDTO;
import com.example.demo.entity.Album;
import com.example.demo.entity.Calendar;
import com.example.demo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Builder
@Getter

@Component
public class EntryConverter {

    public EntryDTO toDto(Calendar calendar) {
        return EntryDTO.builder()
                .date(calendar.getDate())
                .text(calendar.getText())
                .imageUrl(calendar.getImageUrl())
                .build();
    }

    public Calendar toCalendar(EntryDTO dto, Member member) {
        return Calendar.builder()
                .member(member)
                .date(dto.getDate())
                .text(dto.getText())
                .build();
    }

}