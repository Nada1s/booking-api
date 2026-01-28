package com.nada.booking_api.controller;

import com.nada.booking_api.dto.AppointmentCreateRequest;
import com.nada.booking_api.dto.AppointmentResponse;
import com.nada.booking_api.dto.RescheduleRequest;
import com.nada.booking_api.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse book(@Valid @RequestBody AppointmentCreateRequest req) {
        return appointmentService.book(req);
    }

    @GetMapping
    public List<AppointmentResponse> getByDoctorAndDate(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return appointmentService.getByDoctorAndDate(doctorId, date);
    }

    @PutMapping("/{id}/reschedule")
    public AppointmentResponse reschedule(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleRequest req
    ) {
        return appointmentService.reschedule(id, req);
    }
}
