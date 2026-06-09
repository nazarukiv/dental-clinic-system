package com.nazarukiv.dentalclinicsystem.exception;

import java.time.LocalDateTime;

public class DentistAlreadyBookedException extends RuntimeException {

    public DentistAlreadyBookedException(Long dentistId, LocalDateTime appointmentTime) {
        super("Dentist with id " + dentistId + " is already booked at " + appointmentTime);
    }
}
