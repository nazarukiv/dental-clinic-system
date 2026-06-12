package com.nazarukiv.dentalclinicsystem.exception;

public class DentistNotFoundException extends ResourceNotFoundException {

    public DentistNotFoundException(Long dentistId) {
        super("Dentist not found with id: " + dentistId);
    }
}
