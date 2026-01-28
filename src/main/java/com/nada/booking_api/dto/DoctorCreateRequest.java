package com.nada.booking_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DoctorCreateRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        String specialty
) {}
