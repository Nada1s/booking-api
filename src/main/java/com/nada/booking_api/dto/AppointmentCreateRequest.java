package com.nada.booking_api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentCreateRequest(
        @NotNull Long doctorId,
        @NotNull Long patientId,
        @NotNull @Future LocalDateTime startTime,
        @NotNull @Min(5) Integer durationMinutes
) {}
