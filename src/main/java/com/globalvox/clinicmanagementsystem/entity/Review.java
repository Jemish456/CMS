package com.globalvox.clinicmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.globalvox.clinicmanagementsystem.entity.enums.Rating;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double rating;

    private String description;

    @ManyToOne
    @JoinColumn(name="doctor_id", nullable=false)
    @JsonBackReference(value = "doctor")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name="patient_id", nullable=false)
    @JsonBackReference(value = "patient")
    private Patient patient;

}
