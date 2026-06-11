package com.nazarukiv.dentalclinicsystem.service;

import com.nazarukiv.dentalclinicsystem.dto.AppointmentResponse;
import com.nazarukiv.dentalclinicsystem.dto.CreateAppointmentRequest;
import com.nazarukiv.dentalclinicsystem.entity.Appointment;
import com.nazarukiv.dentalclinicsystem.entity.AppointmentStatus;
import com.nazarukiv.dentalclinicsystem.entity.Dentist;
import com.nazarukiv.dentalclinicsystem.entity.Patient;
import com.nazarukiv.dentalclinicsystem.exception.AppointmentNotFoundException;
import com.nazarukiv.dentalclinicsystem.exception.DentistAlreadyBookedException;
import com.nazarukiv.dentalclinicsystem.exception.DentistNotFoundException;
import com.nazarukiv.dentalclinicsystem.exception.PatientNotFoundException;
import com.nazarukiv.dentalclinicsystem.mapper.AppointmentMapper;
import com.nazarukiv.dentalclinicsystem.repository.AppointmentRepository;
import com.nazarukiv.dentalclinicsystem.repository.DentistRepository;
import com.nazarukiv.dentalclinicsystem.repository.PatientRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DentistRepository dentistRepository,
            AppointmentMapper appointmentMapper
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        if (request.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Appointment time must not be null");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException(request.getPatientId()));

        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new DentistNotFoundException(request.getDentistId()));

        if (appointmentRepository.existsByDentist_IdAndAppointmentTime(
                request.getDentistId(),
                request.getAppointmentTime()
        )) {
            throw new DentistAlreadyBookedException(request.getDentistId(), request.getAppointmentTime());
        }

        Appointment appointment = appointmentMapper.toEntity(request, patient, dentist);
        appointment.setStatus(AppointmentStatus.BOOKED);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(savedAppointment);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = findAppointmentById(id);

        return appointmentMapper.toResponse(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException(patientId);
        }

        return mapAppointments(appointmentRepository.findByPatientId(patientId));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDentist(Long dentistId) {
        if (!dentistRepository.existsById(dentistId)) {
            throw new DentistNotFoundException(dentistId);
        }

        return mapAppointments(appointmentRepository.findByDentistId(dentistId));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status) {
        return mapAppointments(appointmentRepository.findByStatus(status));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return mapAppointments(appointmentRepository.findByAppointmentTimeBetween(startOfDay, endOfDay));
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment appointment = findAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Completed appointments cannot be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(cancelledAppointment);
    }

    @Transactional
    public AppointmentResponse completeAppointment(Long id) {
        Appointment appointment = findAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Cancelled appointments cannot be completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment completedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(completedAppointment);
    }

    private Appointment findAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    private List<AppointmentResponse> mapAppointments(List<Appointment> appointments) {
        return appointments.stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }
}
