package com.nada.booking_api.service.impl;

import com.nada.booking_api.dto.AppointmentCreateRequest;
import com.nada.booking_api.dto.AppointmentResponse;
import com.nada.booking_api.dto.RescheduleRequest;
import com.nada.booking_api.entity.Appointment;
import com.nada.booking_api.entity.Doctor;
import com.nada.booking_api.entity.Patient;
import com.nada.booking_api.exception.ConflictException;
import com.nada.booking_api.exception.NotFoundException;
import com.nada.booking_api.repository.AppointmentRepository;
import com.nada.booking_api.repository.DoctorRepository;
import com.nada.booking_api.repository.PatientRepository;
import com.nada.booking_api.service.AppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  DoctorRepository doctorRepository,
                                  PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public AppointmentResponse book(AppointmentCreateRequest req) {
        Doctor doctor = doctorRepository.findById(req.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found: " + req.doctorId()));

        Patient patient = patientRepository.findById(req.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found: " + req.patientId()));

        LocalDateTime start = req.startTime();
        LocalDateTime end = start.plusMinutes(req.durationMinutes());

        boolean overlap = appointmentRepository.existsOverlappingBookedAppointment(
                doctor.getId(), start, end, null
        );
        if (overlap) {
            throw new ConflictException("Appointment overlaps with an existing booking for this doctor");
        }

        Appointment appt = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .startTime(start)
                .durationMinutes(req.durationMinutes())
                .status(Appointment.Status.BOOKED)
                .build();

        Appointment saved = appointmentRepository.save(appt);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getByDoctorAndDate(Long doctorId, LocalDate date) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found: " + doctorId));

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay(); // exclusive end

        return appointmentRepository
                .findByDoctor_IdAndStartTimeBetweenOrderByStartTimeAsc(doctorId, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponse reschedule(Long appointmentId, RescheduleRequest req) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));

        LocalDateTime newStart = req.newStartTime();
        LocalDateTime newEnd = newStart.plusMinutes(req.newDurationMinutes());

        boolean overlap = appointmentRepository.existsOverlappingBookedAppointment(
                appt.getDoctor().getId(), newStart, newEnd, appointmentId
        );

       
        if (overlap) {
            throw new ConflictException("New time overlaps with an existing booking for this doctor");
        }

        appt.setStartTime(newStart);
        appt.setDurationMinutes(req.newDurationMinutes());

        return toResponse(appointmentRepository.save(appt));
    }

    private AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getDoctor().getId(),
                a.getPatient().getId(),
                a.getStartTime(),
                a.getDurationMinutes(),
                a.getStatus().name()
        );
    }
}
