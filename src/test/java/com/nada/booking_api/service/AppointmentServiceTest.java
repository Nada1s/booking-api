package com.nada.booking_api.service;

import com.nada.booking_api.dto.AppointmentCreateRequest;
import com.nada.booking_api.dto.RescheduleRequest;
import com.nada.booking_api.entity.Appointment;
import com.nada.booking_api.entity.Doctor;
import com.nada.booking_api.entity.Patient;
import com.nada.booking_api.exception.ConflictException;
import com.nada.booking_api.exception.NotFoundException;
import com.nada.booking_api.repository.AppointmentRepository;
import com.nada.booking_api.repository.DoctorRepository;
import com.nada.booking_api.repository.PatientRepository;
import com.nada.booking_api.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;

    private AppointmentServiceImpl service;

    @BeforeEach
    void setup() {
        appointmentRepository = mock(AppointmentRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        patientRepository = mock(PatientRepository.class);
        service = new AppointmentServiceImpl(appointmentRepository, doctorRepository, patientRepository);
    }

    @Test
    void book_throwsNotFound_whenDoctorMissing() {
        var req = new AppointmentCreateRequest(1L, 2L, LocalDateTime.now().plusDays(1), 30);
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.book(req));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void book_throwsNotFound_whenPatientMissing() {
        var req = new AppointmentCreateRequest(1L, 2L, LocalDateTime.now().plusDays(1), 30);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(Doctor.builder().id(1L).build()));
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.book(req));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void book_throwsConflict_whenOverlaps() {
        var req = new AppointmentCreateRequest(1L, 2L, LocalDateTime.now().plusDays(1), 30);

        Doctor doctor = Doctor.builder().id(1L).build();
        Patient patient = Patient.builder().id(2L).build();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));

        when(appointmentRepository.existsOverlappingBookedAppointment(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), isNull()
        )).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.book(req));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void book_succeeds_whenNoOverlap() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        var req = new AppointmentCreateRequest(1L, 2L, start, 30);

        Doctor doctor = Doctor.builder().id(1L).build();
        Patient patient = Patient.builder().id(2L).build();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));

        when(appointmentRepository.existsOverlappingBookedAppointment(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), isNull()
        )).thenReturn(false);

        Appointment saved = Appointment.builder()
                .id(10L)
                .doctor(doctor)
                .patient(patient)
                .startTime(start)
                .durationMinutes(30)
                .status(Appointment.Status.BOOKED)
                .build();

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(saved);

        var res = service.book(req);

        assertEquals(10L, res.id());
        assertEquals(1L, res.doctorId());
        assertEquals(2L, res.patientId());
        assertEquals("BOOKED", res.status());
    }

    @Test
    void reschedule_throwsNotFound_whenAppointmentMissing() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());
        var req = new RescheduleRequest(LocalDateTime.now().plusDays(2), 20);

        assertThrows(NotFoundException.class, () -> service.reschedule(99L, req));
    }

    @Test
    void reschedule_throwsConflict_whenOverlaps() {
        Doctor doctor = Doctor.builder().id(1L).build();
        Patient patient = Patient.builder().id(2L).build();

        Appointment existing = Appointment.builder()
                .id(50L)
                .doctor(doctor)
                .patient(patient)
                .startTime(LocalDateTime.now().plusDays(1))
                .durationMinutes(30)
                .status(Appointment.Status.BOOKED)
                .build();

        when(appointmentRepository.findById(50L)).thenReturn(Optional.of(existing));

        when(appointmentRepository.existsOverlappingBookedAppointment(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(50L)
        )).thenReturn(true);

        var req = new RescheduleRequest(LocalDateTime.now().plusDays(2), 30);

        assertThrows(ConflictException.class, () -> service.reschedule(50L, req));
    }
}
