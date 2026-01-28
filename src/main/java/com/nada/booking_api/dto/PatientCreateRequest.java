package com.nada.booking_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PatientCreateRequest(
        @NotBlank String name,
        String phone,
        @NotBlank @Email String email
) {}
