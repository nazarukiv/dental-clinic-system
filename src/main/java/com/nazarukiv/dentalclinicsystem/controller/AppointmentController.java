package com.nazarukiv.dentalclinicsystem.controller;

import com.nazarukiv.dentalclinicsystem.dto.AppointmentResponse;
import com.nazarukiv.dentalclinicsystem.dto.CreateAppointmentRequest;
import com.nazarukiv.dentalclinicsystem.entity.AppointmentStatus;
import com.nazarukiv.dentalclinicsystem.exception.ApiErrorResponse;
import com.nazarukiv.dentalclinicsystem.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
@Validated
@Tag(name = "Appointments", description = "Appointment management endpoints")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Create appointment", description = "Creates a new appointment for a patient and dentist.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient or dentist not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Dentist already booked at the requested time",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request
    ) {
        AppointmentResponse response = appointmentService.createAppointment(request);

        return ResponseEntity
                .created(URI.create("/api/appointments/" + response.getId()))
                .body(response);
    }

    @Operation(summary = "List appointments", description = "Returns all appointments.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments returned successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @Operation(summary = "Get appointment by ID", description = "Returns a single appointment by database ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment returned successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public AppointmentResponse getAppointmentById(
            @Parameter(description = "Appointment ID", example = "1")
            @PathVariable
            @Positive(message = "must be greater than zero") Long id
    ) {
        return appointmentService.getAppointmentById(id);
    }

    @Operation(summary = "List appointments by patient", description = "Returns all appointments for a patient.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments returned successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid patient ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public List<AppointmentResponse> getAppointmentsByPatient(
            @Parameter(description = "Patient ID", example = "1")
            @PathVariable
            @Positive(message = "must be greater than zero") Long patientId
    ) {
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    @Operation(summary = "List appointments by dentist", description = "Returns all appointments for a dentist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments returned successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid dentist ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Dentist not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/dentist/{dentistId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public List<AppointmentResponse> getAppointmentsByDentist(
            @Parameter(description = "Dentist ID", example = "1")
            @PathVariable
            @Positive(message = "must be greater than zero") Long dentistId
    ) {
        return appointmentService.getAppointmentsByDentist(dentistId);
    }

    @Operation(summary = "List appointments by status", description = "Returns all appointments with the given status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments returned successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment status",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public List<AppointmentResponse> getAppointmentsByStatus(
            @Parameter(description = "Appointment status", example = "BOOKED")
            @PathVariable AppointmentStatus status
    ) {
        return appointmentService.getAppointmentsByStatus(status);
    }

    @Operation(summary = "List appointments by date", description = "Returns all appointments scheduled on a date.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments returned successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment date",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public List<AppointmentResponse> getAppointmentsByDate(
            @Parameter(description = "Appointment date in ISO format", example = "2026-06-11")
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return appointmentService.getAppointmentsByDate(date);
    }

    @Operation(summary = "Cancel appointment", description = "Marks an existing appointment as cancelled.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment ID or appointment cannot be cancelled",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public AppointmentResponse cancelAppointment(
            @Parameter(description = "Appointment ID", example = "1")
            @PathVariable
            @Positive(message = "must be greater than zero") Long id
    ) {
        return appointmentService.cancelAppointment(id);
    }

    @Operation(summary = "Complete appointment", description = "Marks an existing appointment as completed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment completed successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment ID or appointment cannot be completed",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public AppointmentResponse completeAppointment(
            @Parameter(description = "Appointment ID", example = "1")
            @PathVariable
            @Positive(message = "must be greater than zero") Long id
    ) {
        return appointmentService.completeAppointment(id);
    }
}
