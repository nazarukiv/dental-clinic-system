package com.nazarukiv.dentalclinicsystem.repository;

import com.nazarukiv.dentalclinicsystem.entity.Appointment;
import com.nazarukiv.dentalclinicsystem.entity.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDentist_IdAndAppointmentTime(Long dentistId, LocalDateTime appointmentTime);

    List<Appointment> findAllByStatus(AppointmentStatus status);

    List<Appointment> findAllByPatientId(Long patientId);
}
