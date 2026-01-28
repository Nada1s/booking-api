package com.nada.booking_api.controller;

import com.nada.booking_api.dto.DoctorCreateRequest;
import com.nada.booking_api.entity.Doctor;
import com.nada.booking_api.exception.NotFoundException;
import com.nada.booking_api.repository.DoctorRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doctor create(@Valid @RequestBody DoctorCreateRequest req) {
        Doctor d = Doctor.builder()
                .name(req.name())
                .email(req.email())
                .specialty(req.specialty())
                .build();
        return doctorRepository.save(d);
    }

    @GetMapping
    public List<Doctor> list() {
        return doctorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Doctor get(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor not found: " + id));
    }
}
