package com.nada.booking_api.dto;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long doctorId,
        Long patientId,
        LocalDateTime startTime,
        Integer durationMinutes,
        String status
) {}
