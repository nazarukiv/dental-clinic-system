package com.nazarukiv.dentalclinicsystem.controller;

import com.nazarukiv.dentalclinicsystem.dto.CreateDentistRequest;
import com.nazarukiv.dentalclinicsystem.dto.DentistResponse;
import com.nazarukiv.dentalclinicsystem.dto.UpdateDentistRequest;
import com.nazarukiv.dentalclinicsystem.exception.ApiErrorResponse;
import com.nazarukiv.dentalclinicsystem.service.DentistService;
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
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dentists")
@Validated
@Tag(name = "Dentists", description = "Dentist management endpoints")
public class DentistController {

    private final DentistService dentistService;

    public DentistController(DentistService dentistService) {
        this.dentistService = dentistService;
    }

    @Operation(summary = "Create dentist", description = "Creates a new dentist record.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dentist created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid dentist request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DentistResponse> createDentist(@Valid @RequestBody CreateDentistRequest request) {
        DentistResponse response = dentistService.createDentist(request);

        return ResponseEntity
                .created(URI.create("/api/dentists/" + response.getId()))
                .body(response);
    }

    @Operation(summary = "List dentists", description = "Returns all dentists.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dentists returned successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<DentistResponse> getAllDentists() {
        return dentistService.getAllDentists();
    }

    @Operation(summary = "Get dentist by ID", description = "Returns a single dentist by database ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dentist returned successfully"),
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
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DentistResponse getDentistById(
            @Parameter(description = "Dentist ID", example = "1")
            @PathVariable
            @Positive(message = "must be greater than zero") Long id
    ) {
        return dentistService.getDentistById(id);
    }

    @Operation(summary = "Update dentist", description = "Updates an existing dentist record.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dentist updated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid dentist request",
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
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DentistResponse updateDentist(
            @Parameter(description = "Dentist ID", example = "1")
            @PathVariable
            @Positive(message = "must be greater than zero") Long id,
            @Valid @RequestBody UpdateDentistRequest request
    ) {
        return dentistService.updateDentist(id, request);
    }

    @Operation(summary = "Delete dentist", description = "Deletes an existing dentist record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dentist deleted successfully"),
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
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDentist(
            @Parameter(description = "Dentist ID", example = "1")
            @PathVariable
            @Positive(message = "must be greater than zero") Long id
    ) {
        dentistService.deleteDentist(id);

        return ResponseEntity.noContent().build();
    }
}
