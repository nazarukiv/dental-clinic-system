package com.nazarukiv.dentalclinicsystem.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateAppointmentRequest {

    @NotNull(message = "must not be null")
    private Long patientId;

    @NotNull(message = "must not be null")
    private Long dentistId;

    @NotNull(message = "must not be null")
    @FutureOrPresent(message = "must be in the present or future")
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
