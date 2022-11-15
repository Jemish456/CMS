package com.globalvox.clinicmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(unique = true)
    @NotBlank
    private String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }
}
