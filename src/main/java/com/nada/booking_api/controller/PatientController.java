package com.nada.booking_api.controller;

import com.nada.booking_api.dto.PatientCreateRequest;
import com.nada.booking_api.entity.Patient;
import com.nada.booking_api.exception.NotFoundException;
import com.nada.booking_api.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@Valid @RequestBody PatientCreateRequest req) {
        Patient p = Patient.builder()
                .name(req.name())
                .phone(req.phone())
                .email(req.email())
                .build();
        return patientRepository.save(p);
    }

    @GetMapping
    public List<Patient> list(@RequestParam(required = false) String name) {
        if (name == null || name.isBlank()) {
            return patientRepository.findAll();
        }
        // simple filter in memory for now; we can optimize with a repo query later
        String q = name.toLowerCase();
        return patientRepository.findAll().stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(q))
                .toList();
    }

    @GetMapping("/{id}")
    public Patient get(@PathVariable Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found: " + id));
    }
}
