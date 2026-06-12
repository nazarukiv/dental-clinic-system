package com.nazarukiv.dentalclinicsystem.exception;

public class AppointmentNotFoundException extends ResourceNotFoundException {

    public AppointmentNotFoundException(Long appointmentId) {
        super("Appointment not found with id: " + appointmentId);
    }
}
