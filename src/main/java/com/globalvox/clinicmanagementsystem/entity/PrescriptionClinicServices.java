package com.globalvox.clinicmanagementsystem.entity;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Data
public class PrescriptionClinicServices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;

    private Double cost;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Appointment appointment;
}
