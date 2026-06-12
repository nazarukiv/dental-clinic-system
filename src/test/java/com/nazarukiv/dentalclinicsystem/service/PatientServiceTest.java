package com.nazarukiv.dentalclinicsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nazarukiv.dentalclinicsystem.entity.Patient;
import com.nazarukiv.dentalclinicsystem.repository.PatientRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void createPatient_success() {
        // Arrange
        Patient patient = createPatient(1L, "Alice", "Brown", "alice@example.com");
        when(patientRepository.save(patient)).thenReturn(patient);

        // Act
        Patient result = patientService.createPatient(patient);

        // Assert
        assertSame(patient, result);
        verify(patientRepository, times(1)).save(patient);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void getPatientById_success() {
        // Arrange
        Long patientId = 1L;
        Patient patient = createPatient(patientId, "Alice", "Brown", "alice@example.com");
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        // Act
        Optional<Patient> result = patientService.getPatientById(patientId);

        // Assert
        assertTrue(result.isPresent());
        assertSame(patient, result.get());
        verify(patientRepository, times(1)).findById(patientId);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void getPatientById_notFound() {
        // Arrange
        Long patientId = 99L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // Act
        Optional<Patient> result = patientService.getPatientById(patientId);

        // Assert
        assertTrue(result.isEmpty());
        verify(patientRepository, times(1)).findById(patientId);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void getAllPatients_success() {
        // Arrange
        List<Patient> patients = List.of(
                createPatient(1L, "Alice", "Brown", "alice@example.com"),
                createPatient(2L, "Bob", "Smith", "bob@example.com")
        );
        when(patientRepository.findAll()).thenReturn(patients);

        // Act
        List<Patient> result = patientService.getAllPatients();

        // Assert
        assertEquals(2, result.size());
        assertSame(patients, result);
        verify(patientRepository, times(1)).findAll();
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void getPatients_success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patients = new PageImpl<>(List.of(
                createPatient(1L, "Alice", "Brown", "alice@example.com")
        ));
        when(patientRepository.findAll(pageable)).thenReturn(patients);

        // Act
        Page<Patient> result = patientService.getPatients(pageable);

        // Assert
        assertSame(patients, result);
        verify(patientRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void getPatientByEmail_success() {
        // Arrange
        String email = "alice@example.com";
        Patient patient = createPatient(1L, "Alice", "Brown", email);
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));

        // Act
        Optional<Patient> result = patientService.getPatientByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertSame(patient, result.get());
        verify(patientRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void patientExistsByEmail_success() {
        // Arrange
        String email = "alice@example.com";
        when(patientRepository.existsByEmail(email)).thenReturn(true);

        // Act
        boolean result = patientService.patientExistsByEmail(email);

        // Assert
        assertTrue(result);
        verify(patientRepository, times(1)).existsByEmail(email);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void getPatientsByLastName_success() {
        // Arrange
        String lastName = "Brown";
        List<Patient> patients = List.of(createPatient(1L, "Alice", lastName, "alice@example.com"));
        when(patientRepository.findByLastName(lastName)).thenReturn(patients);

        // Act
        List<Patient> result = patientService.getPatientsByLastName(lastName);

        // Assert
        assertSame(patients, result);
        verify(patientRepository, times(1)).findByLastName(lastName);
        verifyNoMoreInteractions(patientRepository);
    }

    private Patient createPatient(Long id, String firstName, String lastName, String email) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setPhoneNumber("+1234567890");
        patient.setEmail(email);
        return patient;
    }
}
