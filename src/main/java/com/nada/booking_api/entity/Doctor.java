package com.nada.booking_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors", uniqueConstraints = {
        @UniqueConstraint(name = "uk_doctor_email", columnNames = "email")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String specialty;
}
