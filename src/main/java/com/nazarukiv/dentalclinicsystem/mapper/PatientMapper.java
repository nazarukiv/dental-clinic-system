package com.nazarukiv.dentalclinicsystem.mapper;

import com.nazarukiv.dentalclinicsystem.dto.CreatePatientRequest;
import com.nazarukiv.dentalclinicsystem.dto.PatientResponse;
import com.nazarukiv.dentalclinicsystem.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(CreatePatientRequest request) {
        Patient patient = new Patient();
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setEmail(request.getEmail());
        return patient;
    }

    public PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getPhoneNumber(),
                patient.getEmail()
        );
    }
}
