package com.example.demo.service;

import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import com.example.demo.dto.SettingRequestDTO;


@RequiredArgsConstructor
public class SettingService {

    private final MemberRepository memberRepository;

    public void updateBirthdate(SettingRequestDTO settingReuqestDTO, String email){

    }
    public void updatePassword(SettingRequestDTO settingReuqestDTO, String email){

    }
    public void deleteAccount(SettingRequestDTO settingReuqestDTO, String email){

    }
    public void updatePartnerName(SettingRequestDTO settingReuqestDTO, String email){

    }
    public void updatePartnerBirthdate(SettingRequestDTO settingReuqestDTO, String email){

    }
    public void getProfileInfo(SettingRequestDTO settingReuqestDTO, String email){


    }
}
