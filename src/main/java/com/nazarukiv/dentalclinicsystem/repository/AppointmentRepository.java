package com.nazarukiv.dentalclinicsystem.repository;

import com.nazarukiv.dentalclinicsystem.entity.Appointment;
import com.nazarukiv.dentalclinicsystem.entity.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDentist_IdAndAppointmentTime(Long dentistId, LocalDateTime appointmentTime);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDentistId(Long dentistId);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);
}
