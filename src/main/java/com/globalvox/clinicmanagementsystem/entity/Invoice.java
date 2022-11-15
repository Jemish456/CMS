package com.globalvox.clinicmanagementsystem.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Appointment appointment;

    @OneToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Doctor doctor;

    @OneToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Patient patient;

    private Double totalAmount;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
