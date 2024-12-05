package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    private String username;


    private String email;

    private String friendName;


    private String password;


}
