package com.nada.booking_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patients", uniqueConstraints = {
        @UniqueConstraint(name = "uk_patient_email", columnNames = "email")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(nullable = false, unique = true)
    private String email;
}
