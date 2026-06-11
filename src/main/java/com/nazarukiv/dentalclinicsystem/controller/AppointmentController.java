package com.nazarukiv.dentalclinicsystem.controller;

import com.nazarukiv.dentalclinicsystem.dto.AppointmentResponse;
import com.nazarukiv.dentalclinicsystem.dto.CreateAppointmentRequest;
import com.nazarukiv.dentalclinicsystem.entity.AppointmentStatus;
import com.nazarukiv.dentalclinicsystem.service.AppointmentService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public AppointmentResponse createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        return appointmentService.createAppointment(request);
    }

    @GetMapping
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public AppointmentResponse getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<AppointmentResponse> getAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    @GetMapping("/dentist/{dentistId}")
    public List<AppointmentResponse> getAppointmentsByDentist(@PathVariable Long dentistId) {
        return appointmentService.getAppointmentsByDentist(dentistId);
    }

    @GetMapping("/status/{status}")
    public List<AppointmentResponse> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        return appointmentService.getAppointmentsByStatus(status);
    }

    @GetMapping("/date/{date}")
    public List<AppointmentResponse> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return appointmentService.getAppointmentsByDate(date);
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancelAppointment(@PathVariable Long id) {
        return appointmentService.cancelAppointment(id);
    }

    @PatchMapping("/{id}/complete")
    public AppointmentResponse completeAppointment(@PathVariable Long id) {
        return appointmentService.completeAppointment(id);
    }
}
