package com.nazarukiv.dentalclinicsystem.service;

import com.nazarukiv.dentalclinicsystem.entity.Appointment;
import com.nazarukiv.dentalclinicsystem.entity.AppointmentStatus;
import com.nazarukiv.dentalclinicsystem.entity.Dentist;
import com.nazarukiv.dentalclinicsystem.entity.Patient;
import com.nazarukiv.dentalclinicsystem.exception.DentistAlreadyBookedException;
import com.nazarukiv.dentalclinicsystem.exception.DentistNotFoundException;
import com.nazarukiv.dentalclinicsystem.exception.PatientNotFoundException;
import com.nazarukiv.dentalclinicsystem.repository.AppointmentRepository;
import com.nazarukiv.dentalclinicsystem.repository.DentistRepository;
import com.nazarukiv.dentalclinicsystem.repository.PatientRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DentistRepository dentistRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
    }

    public Appointment bookAppointment(Long patientId, Long dentistId, LocalDateTime appointmentTime) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        Dentist dentist = dentistRepository.findById(dentistId)
                .orElseThrow(() -> new DentistNotFoundException(dentistId));

        boolean dentistAlreadyBooked = appointmentRepository.existsByDentist_IdAndAppointmentTime(
                dentistId,
                appointmentTime
        );

        if (dentistAlreadyBooked) {
            throw new DentistAlreadyBookedException(dentistId, appointmentTime);
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(AppointmentStatus.BOOKED);

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findAllByStatus(status);
    }

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findAllByPatientId(patientId);
    }
}
