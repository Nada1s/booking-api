package com.nada.booking_api.service;

import com.nada.booking_api.dto.AppointmentCreateRequest;
import com.nada.booking_api.dto.AppointmentResponse;
import com.nada.booking_api.dto.RescheduleRequest;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse book(AppointmentCreateRequest req);
    List<AppointmentResponse> getByDoctorAndDate(Long doctorId, LocalDate date);
    AppointmentResponse reschedule(Long appointmentId, RescheduleRequest req);
}
