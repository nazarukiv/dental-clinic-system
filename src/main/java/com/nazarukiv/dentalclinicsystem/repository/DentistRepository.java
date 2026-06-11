package com.nazarukiv.dentalclinicsystem.repository;

import com.nazarukiv.dentalclinicsystem.entity.Dentist;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DentistRepository extends JpaRepository<Dentist, Long> {

    boolean existsByEmail(String email);

    Optional<Dentist> findByEmail(String email);
}
