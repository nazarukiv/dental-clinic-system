package com.nazarukiv.dentalclinicsystem.repository;

import com.nazarukiv.dentalclinicsystem.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
