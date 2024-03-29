package com.globalvox.clinicmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ClinicServices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private Double cost;
}
