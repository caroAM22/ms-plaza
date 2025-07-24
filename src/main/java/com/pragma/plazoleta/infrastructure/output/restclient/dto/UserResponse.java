package com.pragma.plazoleta.infrastructure.output.restclient.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserResponse {
    private String id;
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String roleId;
} 