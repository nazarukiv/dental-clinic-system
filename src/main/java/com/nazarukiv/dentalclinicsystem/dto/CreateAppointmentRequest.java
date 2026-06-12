package com.nazarukiv.dentalclinicsystem.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public class CreateAppointmentRequest {

    @NotNull(message = "must not be null")
    @Positive(message = "must be greater than zero")
    private Long patientId;

    @NotNull(message = "must not be null")
    @Positive(message = "must be greater than zero")
    private Long dentistId;

    @NotNull(message = "must not be null")
    @Future(message = "must be in the future")
    private LocalDateTime appointmentTime;

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
}
