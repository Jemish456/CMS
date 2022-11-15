package com.globalvox.clinicmanagementsystem.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;


@Table(name = "states")
public class State {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Country country;

    public State() {

    }

    public State(String name, Country country) {
        this.name = name;
        this.country = country;
    }
}
