package com.nazarukiv.dentalclinicsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nazarukiv.dentalclinicsystem.dto.AppointmentResponse;
import com.nazarukiv.dentalclinicsystem.dto.CreateAppointmentRequest;
import com.nazarukiv.dentalclinicsystem.entity.Appointment;
import com.nazarukiv.dentalclinicsystem.entity.AppointmentStatus;
import com.nazarukiv.dentalclinicsystem.entity.Dentist;
import com.nazarukiv.dentalclinicsystem.entity.Patient;
import com.nazarukiv.dentalclinicsystem.entity.Specialisation;
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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void createAppointment_success() {
        // Arrange
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1);
        CreateAppointmentRequest request = createAppointmentRequest(1L, 2L, appointmentTime);
        Patient patient = createPatient(request.getPatientId());
        Dentist dentist = createDentist(request.getDentistId());
        Appointment appointment = createAppointment(null, patient, dentist, appointmentTime, null);
        AppointmentResponse response = createAppointmentResponse(10L, AppointmentStatus.BOOKED);

        when(patientRepository.findById(request.getPatientId())).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(request.getDentistId())).thenReturn(Optional.of(dentist));
        when(appointmentRepository.existsByDentist_IdAndAppointmentTime(
                request.getDentistId(),
                request.getAppointmentTime()
        )).thenReturn(false);
        when(appointmentMapper.toEntity(request, patient, dentist)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        // Act
        AppointmentResponse result = appointmentService.createAppointment(request);

        // Assert
        assertSame(response, result);
        assertEquals(AppointmentStatus.BOOKED, appointment.getStatus());
        verify(patientRepository, times(1)).findById(request.getPatientId());
        verify(dentistRepository, times(1)).findById(request.getDentistId());
        verify(appointmentRepository, times(1)).existsByDentist_IdAndAppointmentTime(
                request.getDentistId(),
                request.getAppointmentTime()
        );
        verify(appointmentMapper, times(1)).toEntity(request, patient, dentist);
        verify(appointmentRepository, times(1)).save(appointment);
        verify(appointmentMapper, times(1)).toResponse(appointment);
        verifyNoMoreInteractions(patientRepository, dentistRepository, appointmentRepository, appointmentMapper);
    }

    @Test
    void createAppointment_nullAppointmentTimeThrowsException() {
        // Arrange
        CreateAppointmentRequest request = createAppointmentRequest(1L, 2L, null);

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> appointmentService.createAppointment(request));
        verifyNoInteractions(patientRepository, dentistRepository, appointmentRepository, appointmentMapper);
    }

    @Test
    void createAppointment_patientNotFound() {
        // Arrange
        CreateAppointmentRequest request = createAppointmentRequest(99L, 2L, LocalDateTime.now().plusDays(1));
        when(patientRepository.findById(request.getPatientId())).thenReturn(Optional.empty());

        // Act / Assert
        assertThrows(PatientNotFoundException.class, () -> appointmentService.createAppointment(request));
        verify(patientRepository, times(1)).findById(request.getPatientId());
        verifyNoInteractions(dentistRepository, appointmentRepository, appointmentMapper);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void createAppointment_dentistNotFound() {
        // Arrange
        CreateAppointmentRequest request = createAppointmentRequest(1L, 99L, LocalDateTime.now().plusDays(1));
        Patient patient = createPatient(request.getPatientId());

        when(patientRepository.findById(request.getPatientId())).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(request.getDentistId())).thenReturn(Optional.empty());

        // Act / Assert
        assertThrows(DentistNotFoundException.class, () -> appointmentService.createAppointment(request));
        verify(patientRepository, times(1)).findById(request.getPatientId());
        verify(dentistRepository, times(1)).findById(request.getDentistId());
        verifyNoInteractions(appointmentRepository, appointmentMapper);
        verifyNoMoreInteractions(patientRepository, dentistRepository);
    }

    @Test
    void createAppointment_doubleBookingConflict() {
        // Arrange
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1);
        CreateAppointmentRequest request = createAppointmentRequest(1L, 2L, appointmentTime);
        Patient patient = createPatient(request.getPatientId());
        Dentist dentist = createDentist(request.getDentistId());

        when(patientRepository.findById(request.getPatientId())).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(request.getDentistId())).thenReturn(Optional.of(dentist));
        when(appointmentRepository.existsByDentist_IdAndAppointmentTime(
                request.getDentistId(),
                request.getAppointmentTime()
        )).thenReturn(true);

        // Act / Assert
        assertThrows(DentistAlreadyBookedException.class, () -> appointmentService.createAppointment(request));
        verify(patientRepository, times(1)).findById(request.getPatientId());
        verify(dentistRepository, times(1)).findById(request.getDentistId());
        verify(appointmentRepository, times(1)).existsByDentist_IdAndAppointmentTime(
                request.getDentistId(),
                request.getAppointmentTime()
        );
        verifyNoInteractions(appointmentMapper);
        verifyNoMoreInteractions(patientRepository, dentistRepository, appointmentRepository);
    }

    @Test
    void getAppointmentById_success() {
        // Arrange
        Long appointmentId = 10L;
        Appointment appointment = createAppointment(
                appointmentId,
                createPatient(1L),
                createDentist(2L),
                LocalDateTime.now().plusDays(1),
                AppointmentStatus.BOOKED
        );
        AppointmentResponse response = createAppointmentResponse(appointmentId, AppointmentStatus.BOOKED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        // Act
        AppointmentResponse result = appointmentService.getAppointmentById(appointmentId);

        // Assert
        assertSame(response, result);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentMapper, times(1)).toResponse(appointment);
        verifyNoMoreInteractions(appointmentRepository, appointmentMapper);
        verifyNoInteractions(patientRepository, dentistRepository);
    }

    @Test
    void getAllAppointments_success() {
        // Arrange
        Appointment firstAppointment = createAppointment(
                10L,
                createPatient(1L),
                createDentist(2L),
                LocalDateTime.now().plusDays(1),
                AppointmentStatus.BOOKED
        );
        Appointment secondAppointment = createAppointment(
                11L,
                createPatient(3L),
                createDentist(4L),
                LocalDateTime.now().plusDays(2),
                AppointmentStatus.COMPLETED
        );
        AppointmentResponse firstResponse = createAppointmentResponse(10L, AppointmentStatus.BOOKED);
        AppointmentResponse secondResponse = createAppointmentResponse(11L, AppointmentStatus.COMPLETED);

        when(appointmentRepository.findAll()).thenReturn(List.of(firstAppointment, secondAppointment));
        when(appointmentMapper.toResponse(firstAppointment)).thenReturn(firstResponse);
        when(appointmentMapper.toResponse(secondAppointment)).thenReturn(secondResponse);

        // Act
        List<AppointmentResponse> result = appointmentService.getAllAppointments();

        // Assert
        assertEquals(List.of(firstResponse, secondResponse), result);
        verify(appointmentRepository, times(1)).findAll();
        verify(appointmentMapper, times(1)).toResponse(firstAppointment);
        verify(appointmentMapper, times(1)).toResponse(secondAppointment);
        verifyNoMoreInteractions(appointmentRepository, appointmentMapper);
        verifyNoInteractions(patientRepository, dentistRepository);
    }

    @Test
    void getAppointmentsByPatient_success() {
        // Arrange
        Long patientId = 1L;
        Appointment appointment = createAppointment(
                10L,
                createPatient(patientId),
                createDentist(2L),
                LocalDateTime.now().plusDays(1),
                AppointmentStatus.BOOKED
        );
        AppointmentResponse response = createAppointmentResponse(10L, AppointmentStatus.BOOKED);

        when(patientRepository.existsById(patientId)).thenReturn(true);
        when(appointmentRepository.findByPatientId(patientId)).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        // Act
        List<AppointmentResponse> result = appointmentService.getAppointmentsByPatient(patientId);

        // Assert
        assertEquals(List.of(response), result);
        verify(patientRepository, times(1)).existsById(patientId);
        verify(appointmentRepository, times(1)).findByPatientId(patientId);
        verify(appointmentMapper, times(1)).toResponse(appointment);
        verifyNoMoreInteractions(patientRepository, appointmentRepository, appointmentMapper);
        verifyNoInteractions(dentistRepository);
    }

    @Test
    void getAppointmentsByPatient_patientNotFound() {
        // Arrange
        Long patientId = 99L;
        when(patientRepository.existsById(patientId)).thenReturn(false);

        // Act / Assert
        assertThrows(PatientNotFoundException.class, () -> appointmentService.getAppointmentsByPatient(patientId));
        verify(patientRepository, times(1)).existsById(patientId);
        verifyNoInteractions(dentistRepository, appointmentRepository, appointmentMapper);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void getAppointmentsByDentist_success() {
        // Arrange
        Long dentistId = 2L;
        Appointment appointment = createAppointment(
                10L,
                createPatient(1L),
                createDentist(dentistId),
                LocalDateTime.now().plusDays(1),
                AppointmentStatus.BOOKED
        );
        AppointmentResponse response = createAppointmentResponse(10L, AppointmentStatus.BOOKED);

        when(dentistRepository.existsById(dentistId)).thenReturn(true);
        when(appointmentRepository.findByDentistId(dentistId)).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        // Act
        List<AppointmentResponse> result = appointmentService.getAppointmentsByDentist(dentistId);

        // Assert
        assertEquals(List.of(response), result);
        verify(dentistRepository, times(1)).existsById(dentistId);
        verify(appointmentRepository, times(1)).findByDentistId(dentistId);
        verify(appointmentMapper, times(1)).toResponse(appointment);
        verifyNoMoreInteractions(dentistRepository, appointmentRepository, appointmentMapper);
        verifyNoInteractions(patientRepository);
    }

    @Test
    void getAppointmentsByDentist_dentistNotFound() {
        // Arrange
        Long dentistId = 99L;
        when(dentistRepository.existsById(dentistId)).thenReturn(false);

        // Act / Assert
        assertThrows(DentistNotFoundException.class, () -> appointmentService.getAppointmentsByDentist(dentistId));
        verify(dentistRepository, times(1)).existsById(dentistId);
        verifyNoInteractions(patientRepository, appointmentRepository, appointmentMapper);
        verifyNoMoreInteractions(dentistRepository);
    }

    @Test
    void getAppointmentsByStatus_success() {
        // Arrange
        AppointmentStatus status = AppointmentStatus.BOOKED;
        Appointment appointment = createAppointment(
                10L,
                createPatient(1L),
                createDentist(2L),
                LocalDateTime.now().plusDays(1),
                status
        );
        AppointmentResponse response = createAppointmentResponse(10L, status);

        when(appointmentRepository.findByStatus(status)).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        // Act
        List<AppointmentResponse> result = appointmentService.getAppointmentsByStatus(status);

        // Assert
        assertEquals(List.of(response), result);
        verify(appointmentRepository, times(1)).findByStatus(status);
        verify(appointmentMapper, times(1)).toResponse(appointment);
        verifyNoMoreInteractions(appointmentRepository, appointmentMapper);
        verifyNoInteractions(patientRepository, dentistRepository);
    }

    @Test
    void getAppointmentsByDate_success() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 6, 11);
        LocalDateTime appointmentTime = date.atTime(9, 30);
        Appointment appointment = createAppointment(
                10L,
                createPatient(1L),
                createDentist(2L),
                appointmentTime,
                AppointmentStatus.BOOKED
        );
        AppointmentResponse response = createAppointmentResponse(10L, AppointmentStatus.BOOKED);

        when(appointmentRepository.findByAppointmentTimeBetween(
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX)
        )).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        // Act
        List<AppointmentResponse> result = appointmentService.getAppointmentsByDate(date);

        // Assert
        assertEquals(List.of(response), result);
        verify(appointmentRepository, times(1)).findByAppointmentTimeBetween(
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX)
        );
        verify(appointmentMapper, times(1)).toResponse(appointment);
        verifyNoMoreInteractions(appointmentRepository, appointmentMapper);
        verifyNoInteractions(patientRepository, dentistRepository);
    }

    @Test
    void cancelAppointment_success() {
        // Arrange
        Long appointmentId = 10L;
        Appointment appointment = createAppointment(
                appointmentId,
                createPatient(1L),
                createDentist(2L),
                LocalDateTime.now().plusDays(1),
                AppointmentStatus.BOOKED
        );
        AppointmentResponse response = createAppointmentResponse(appointmentId, AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        // Act
        AppointmentResponse result = appointmentService.cancelAppointment(appointmentId);

        // Assert
        assertSame(response, result);
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).save(appointment);
        verify(appointmentMapper, times(1)).toResponse(appointment);
        verifyNoMoreInteractions(appointmentRepository, appointmentMapper);
        verifyNoInteractions(patientRepository, dentistRepository);
    }

    @Test
    void cancelAppointment_completedAppointmentThrowsException() {
        // Arrange
        Long appointmentId = 10L;
        Appointment appointment = createAppointment(
                appointmentId,
                createPatient(1L),
                createDentist(2L),
                LocalDateTime.now().plusDays(1),
                AppointmentStatus.COMPLETED
        );

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> appointmentService.cancelAppointment(appointmentId));
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(patientRepository, dentistRepository, appointmentMapper);
    }

    @Test
    void completeAppointment_success() {
        // Arrange
        Long appointmentId = 10L;
        Appointment appointment = createAppointment(
                appointmentId,
                createPatient(1L),
                createDentist(2L),
                LocalDateTime.now().plusDays(1),
                AppointmentStatus.BOOKED
        );
        AppointmentResponse response = createAppointmentResponse(appointmentId, AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        // Act
        AppointmentResponse result = appointmentService.completeAppointment(appointmentId);

        // Assert
        assertSame(response, result);
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).save(appointment);
        verify(appointmentMapper, times(1)).toResponse(appointment);
        verifyNoMoreInteractions(appointmentRepository, appointmentMapper);
        verifyNoInteractions(patientRepository, dentistRepository);
    }

    @Test
    void completeAppointment_cancelledAppointmentThrowsException() {
        // Arrange
        Long appointmentId = 10L;
        Appointment appointment = createAppointment(
                appointmentId,
                createPatient(1L),
                createDentist(2L),
                LocalDateTime.now().plusDays(1),
                AppointmentStatus.CANCELLED
        );

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> appointmentService.completeAppointment(appointmentId));
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(patientRepository, dentistRepository, appointmentMapper);
    }

    @Test
    void getAppointmentById_notFound() {
        // Arrange
        Long appointmentId = 99L;
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act / Assert
        assertThrows(AppointmentNotFoundException.class, () -> appointmentService.getAppointmentById(appointmentId));
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verifyNoInteractions(patientRepository, dentistRepository, appointmentMapper);
        verifyNoMoreInteractions(appointmentRepository);
    }

    private CreateAppointmentRequest createAppointmentRequest(
            Long patientId,
            Long dentistId,
            LocalDateTime appointmentTime
    ) {
        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.setPatientId(patientId);
        request.setDentistId(dentistId);
        request.setAppointmentTime(appointmentTime);
        return request;
    }

    private Patient createPatient(Long id) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setFirstName("Alice");
        patient.setLastName("Brown");
        patient.setPhoneNumber("+1234567890");
        patient.setEmail("alice@example.com");
        return patient;
    }

    private Dentist createDentist(Long id) {
        Dentist dentist = new Dentist();
        dentist.setId(id);
        dentist.setFirstName("Dana");
        dentist.setLastName("White");
        dentist.setSpecialisation(Specialisation.ORTHODONTICS);
        dentist.setEmail("dana@example.com");
        return dentist;
    }

    private Appointment createAppointment(
            Long id,
            Patient patient,
            Dentist dentist,
            LocalDateTime appointmentTime,
            AppointmentStatus status
    ) {
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(status);
        return appointment;
    }

    private AppointmentResponse createAppointmentResponse(Long id, AppointmentStatus status) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(id);
        response.setPatientId(1L);
        response.setPatientFullName("Alice Brown");
        response.setDentistId(2L);
        response.setDentistFullName("Dana White");
        response.setAppointmentTime(LocalDateTime.now().plusDays(1));
        response.setStatus(status);
        return response;
    }
}
