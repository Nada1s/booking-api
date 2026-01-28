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

   @Query(value = """
    SELECT EXISTS(
      SELECT 1 FROM appointments a
      WHERE a.doctor_id = :doctorId
        AND a.status = 'BOOKED'
        AND (:excludeId IS NULL OR a.id <> :excludeId)
        AND a.start_time < :newEnd
        AND DATE_ADD(a.start_time, INTERVAL a.duration_minutes MINUTE) > :newStart
    )
""", nativeQuery = true)
boolean existsOverlappingBookedAppointment(
        @Param("doctorId") Long doctorId,
        @Param("newStart") LocalDateTime newStart,
        @Param("newEnd") LocalDateTime newEnd,
        @Param("excludeId") Long excludeId
);
}
