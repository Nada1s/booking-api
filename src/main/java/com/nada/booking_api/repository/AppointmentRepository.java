package com.nada.booking_api.repository;

import com.nada.booking_api.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctor_IdAndStartTimeBetweenOrderByStartTimeAsc(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        select count(a) > 0 from Appointment a
        where a.doctor.id = :doctorId
          and a.status = com.nada.booking_api.entity.Appointment.Status.BOOKED
          and a.startTime < :newEnd
          and (a.startTime + (a.durationMinutes * 1L) * 1 minute) > :newStart
    """)
    boolean existsOverlappingBookedAppointment(
            @Param("doctorId") Long doctorId,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd
    );
}
