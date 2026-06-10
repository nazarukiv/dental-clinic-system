package com.nazarukiv.dentalclinicsystem.controller;

import com.nazarukiv.dentalclinicsystem.dto.CreatePatientRequest;
import com.nazarukiv.dentalclinicsystem.dto.PatientResponse;
import com.nazarukiv.dentalclinicsystem.entity.Patient;
import com.nazarukiv.dentalclinicsystem.exception.PatientNotFoundException;
import com.nazarukiv.dentalclinicsystem.mapper.PatientMapper;
import com.nazarukiv.dentalclinicsystem.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    public PatientController(PatientService patientService, PatientMapper patientMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    @GetMapping
    public Page<PatientResponse> getPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Patient> patients = patientService.getPatients(pageable);

        return patients.map(patientMapper::toResponse);
    }

    @GetMapping("/{id}")
    public PatientResponse getPatientById(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        return patientMapper.toResponse(patient);
    }

    @PostMapping
    public PatientResponse createPatient(@Valid @RequestBody CreatePatientRequest request) {
        Patient patient = patientMapper.toEntity(request);
        Patient savedPatient = patientService.createPatient(patient);

        return patientMapper.toResponse(savedPatient);
    }
}
