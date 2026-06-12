package com.nazarukiv.dentalclinicsystem.controller;

import com.nazarukiv.dentalclinicsystem.dto.CreatePatientRequest;
import com.nazarukiv.dentalclinicsystem.dto.PatientResponse;
import com.nazarukiv.dentalclinicsystem.entity.Patient;
import com.nazarukiv.dentalclinicsystem.exception.ApiErrorResponse;
import com.nazarukiv.dentalclinicsystem.exception.PatientNotFoundException;
import com.nazarukiv.dentalclinicsystem.mapper.PatientMapper;
import com.nazarukiv.dentalclinicsystem.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
@Validated
@Tag(name = "Patients", description = "Patient management endpoints")
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    public PatientController(PatientService patientService, PatientMapper patientMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    @Operation(summary = "List patients", description = "Returns a paginated list of patients.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patients returned successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameter",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public Page<PatientResponse> getPatients(
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "must be zero or greater") int page,

            @Parameter(description = "Number of patients per page", example = "10")
            @RequestParam(defaultValue = "10")
            @Positive(message = "must be greater than zero") int size,

            @Parameter(description = "Patient field used for sorting", example = "id")
            @RequestParam(defaultValue = "id")
            @NotBlank(message = "must not be blank")
            @Size(max = 50, message = "must be at most 50 characters") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Patient> patients = patientService.getPatients(pageable);

        return patients.map(patientMapper::toResponse);
    }

    @Operation(summary = "Get patient by ID", description = "Returns a single patient by database ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient returned successfully"),
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
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public PatientResponse getPatientById(
            @Parameter(description = "Patient ID", example = "1")
            @PathVariable
            @Positive(message = "must be greater than zero") Long id
    ) {
        Patient patient = patientService.getPatientById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        return patientMapper.toResponse(patient);
    }

    @Operation(summary = "Create patient", description = "Creates a new patient record.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid patient request",
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
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        Patient patient = patientMapper.toEntity(request);
        Patient savedPatient = patientService.createPatient(patient);
        PatientResponse response = patientMapper.toResponse(savedPatient);

        return ResponseEntity
                .created(URI.create("/api/patients/" + response.getId()))
                .body(response);
    }
}
