package com.nazarukiv.dentalclinicsystem.controller;

import com.nazarukiv.dentalclinicsystem.dto.CreateDentistRequest;
import com.nazarukiv.dentalclinicsystem.dto.DentistResponse;
import com.nazarukiv.dentalclinicsystem.dto.UpdateDentistRequest;
import com.nazarukiv.dentalclinicsystem.service.DentistService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
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
public class DentistController {

    private final DentistService dentistService;

    public DentistController(DentistService dentistService) {
        this.dentistService = dentistService;
    }

    @PostMapping
    public DentistResponse createDentist(@Valid @RequestBody CreateDentistRequest request) {
        return dentistService.createDentist(request);
    }

    @GetMapping
    public List<DentistResponse> getAllDentists() {
        return dentistService.getAllDentists();
    }

    @GetMapping("/{id}")
    public DentistResponse getDentistById(@PathVariable Long id) {
        return dentistService.getDentistById(id);
    }

    @PutMapping("/{id}")
    public DentistResponse updateDentist(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDentistRequest request
    ) {
        return dentistService.updateDentist(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDentist(@PathVariable Long id) {
        dentistService.deleteDentist(id);

        return ResponseEntity.noContent().build();
    }
}
