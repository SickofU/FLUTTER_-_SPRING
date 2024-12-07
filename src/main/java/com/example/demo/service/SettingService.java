package com.example.demo.service;

import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import com.example.demo.dto.SettingRequestDTO;


@RequiredArgsConstructor
public class SettingService {

    private final MemberRepository memberRepository;


}
