package com.nazarukiv.dentalclinicsystem.exception;

public class PatientNotFoundException extends ResourceNotFoundException {

    public PatientNotFoundException(Long patientId) {
        super("Patient not found with id: " + patientId);
    }
}
