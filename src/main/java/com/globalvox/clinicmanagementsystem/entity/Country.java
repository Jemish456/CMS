package com.globalvox.clinicmanagementsystem.entity;

import javax.persistence.*;


@Table(name = "countries")
public class Country {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Country() {
    }
}
