package com.nazarukiv.dentalclinicsystem.controller;

import com.nazarukiv.dentalclinicsystem.dto.CreatePatientRequest;
import com.nazarukiv.dentalclinicsystem.dto.PatientResponse;
import com.nazarukiv.dentalclinicsystem.entity.Patient;
import com.nazarukiv.dentalclinicsystem.exception.PatientNotFoundException;
import com.nazarukiv.dentalclinicsystem.mapper.PatientMapper;
import com.nazarukiv.dentalclinicsystem.service.PatientService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public List<PatientResponse> getAllPatients() {
        return patientService.getAllPatients()
                .stream()
                .map(patientMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PatientResponse getPatientById(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        return patientMapper.toResponse(patient);
    }

    @PostMapping
    public PatientResponse createPatient(@RequestBody CreatePatientRequest request) {
        Patient patient = patientMapper.toEntity(request);
        Patient savedPatient = patientService.createPatient(patient);

        return patientMapper.toResponse(savedPatient);
    }
}
