package com.nada.booking_api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleRequest(
        @NotNull @Future LocalDateTime newStartTime,
        @NotNull @Min(5) Integer newDurationMinutes
) {}
