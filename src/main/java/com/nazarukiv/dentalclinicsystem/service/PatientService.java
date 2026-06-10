package com.nazarukiv.dentalclinicsystem.service;

import com.nazarukiv.dentalclinicsystem.entity.Patient;
import com.nazarukiv.dentalclinicsystem.repository.PatientRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Page<Patient> getPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    public boolean patientExistsByEmail(String email) {
        return patientRepository.existsByEmail(email);
    }

    public List<Patient> getPatientsByLastName(String lastName) {
        return patientRepository.findByLastName(lastName);
    }
}
