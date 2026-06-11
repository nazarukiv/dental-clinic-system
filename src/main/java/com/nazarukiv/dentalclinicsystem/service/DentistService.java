package com.nazarukiv.dentalclinicsystem.service;

import com.nazarukiv.dentalclinicsystem.dto.CreateDentistRequest;
import com.nazarukiv.dentalclinicsystem.dto.DentistResponse;
import com.nazarukiv.dentalclinicsystem.dto.UpdateDentistRequest;
import com.nazarukiv.dentalclinicsystem.entity.Dentist;
import com.nazarukiv.dentalclinicsystem.exception.DentistNotFoundException;
import com.nazarukiv.dentalclinicsystem.mapper.DentistMapper;
import com.nazarukiv.dentalclinicsystem.repository.DentistRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DentistService {

    private final DentistRepository dentistRepository;
    private final DentistMapper dentistMapper;

    public DentistService(DentistRepository dentistRepository, DentistMapper dentistMapper) {
        this.dentistRepository = dentistRepository;
        this.dentistMapper = dentistMapper;
    }

    public DentistResponse createDentist(CreateDentistRequest request) {
        if (dentistRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Dentist with email already exists: " + request.getEmail());
        }

        Dentist dentist = dentistMapper.toEntity(request);
        Dentist savedDentist = dentistRepository.save(dentist);

        return dentistMapper.toResponse(savedDentist);
    }

    public DentistResponse getDentistById(Long id) {
        Dentist dentist = findDentistById(id);

        return dentistMapper.toResponse(dentist);
    }

    public List<DentistResponse> getAllDentists() {
        return dentistRepository.findAll()
                .stream()
                .map(dentistMapper::toResponse)
                .toList();
    }

    public DentistResponse updateDentist(Long id, UpdateDentistRequest request) {
        Dentist dentist = findDentistById(id);

        dentistRepository.findByEmail(request.getEmail())
                .filter(existingDentist -> !existingDentist.getId().equals(id))
                .ifPresent(existingDentist -> {
                    throw new IllegalArgumentException("Dentist with email already exists: " + request.getEmail());
                });

        dentist.setFirstName(request.getFirstName());
        dentist.setLastName(request.getLastName());
        dentist.setSpecialisation(request.getSpecialisation());
        dentist.setEmail(request.getEmail());

        Dentist updatedDentist = dentistRepository.save(dentist);

        return dentistMapper.toResponse(updatedDentist);
    }

    public void deleteDentist(Long id) {
        Dentist dentist = findDentistById(id);

        dentistRepository.delete(dentist);
    }

    private Dentist findDentistById(Long id) {
        return dentistRepository.findById(id)
                .orElseThrow(() -> new DentistNotFoundException(id));
    }
}
