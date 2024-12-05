package com.example.demo.repository;

import com.example.demo.entity.Album;
import com.example.demo.entity.Calendar;
import com.example.demo.entity.Friendship;
import com.example.demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findByMemberAndDate(Member member, LocalDate date);
    List<Calendar> findByMemberInAndDate(List<Member> member, LocalDate date);



}

