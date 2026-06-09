package com.nazarukiv.dentalclinicsystem.repository;

import com.nazarukiv.dentalclinicsystem.entity.Appointment;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDentist_IdAndAppointmentTime(Long dentistId, LocalDateTime appointmentTime);
}
