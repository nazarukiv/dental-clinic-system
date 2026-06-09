package com.nazarukiv.dentalclinicsystem.repository;

import com.nazarukiv.dentalclinicsystem.entity.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DentistRepository extends JpaRepository<Dentist, Long> {
}
